package it.polimi.rsp.test.mock;

import it.polimi.rsp.server.exceptions.DuplicateException;
import it.polimi.rsp.server.exceptions.ResourceNotFound;
import it.polimi.rsp.server.exceptions.ServiceException;
import it.polimi.rsp.server.model.Key;
import it.polimi.rsp.server.model.KeyFactory;
import it.polimi.rsp.server.model.StatusManager;
import it.polimi.rsp.server.model.Stream;
import it.polimi.rsp.server.web.*;
import it.polimi.rsp.test.mock.features.*;
import it.polimi.rsp.test.mock.model.EmptyTask;
import it.polimi.rsp.test.mock.model.InStream;
import it.polimi.rsp.test.mock.model.Query;
import it.polimi.rsp.test.mock.model.QueryBody;
import it.polimi.rsp.utils.URIUtils;
import it.polimi.rsp.vocals.annotations.services.ProcessingService;
import lombok.Getter;
import lombok.extern.java.Log;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.polimi.rsp.utils.URIUtils.cleanProtocols;

@Log
@Getter
@ProcessingService(host = "localhost", port = 8181)
public class MockEngine implements QueryRegistrationFeature, QueryDeletionFeature, StreamRegistrationFeature, StreamGetterFeature, StreamsGetterFeature, StreamDeletionFeature {

    private final String base;
    private final String name;

    private final InternalEngine engine = new InternalEngine();

    public MockEngine(String name, String base) {
        this.name = name;
        this.base = base;
    }

    @Override
    public InStream register_stream(String id1, String cc) {
        try {
            String id = cleanProtocols(id1);
            InStream stream = new InStream(getStreamUri(id), cc);
            Source source = SourceSinkFactory.webSocket(stream);
            FeedStreamTask task = new FeedStreamTask(stream);
            source.task(task);

            //TODO uri creation structured with vocals
            Key k = createStreamKey(id);
            StatusManager.commit(k, stream);
            Key k1 = KeyFactory.create(k);
            StatusManager.commit(k1, source, task);
            return stream;
        } catch (DuplicateException e) {
            throw new ServiceException(e);
        }
    }


    @Override
    public Query register_query(QueryBody body) {
        try {

            Key k = createQueryKey(body.id);
            Key subkey = KeyFactory.create(k);


            //Check whether the streams are registered

            //Register the query to the engine
            EmptyTask queryTask = engine.register_query(body);

            //Retrieve the streams and create if unavailable

            List<Stream> source_streams = new ArrayList<>();
            for (String s : body.input_streams) {
                Stream stream = StatusManager
                        .getStream(getStreamKey(s))
                        .orElseGet(() -> register_stream(s, s));
                source_streams.add(stream);
            }

            //Since we want to consumer the output of this query as a stream; We attach an internal sink and we connect it
            // using a proxy

            //These can be deleted using subkey as key
            Stream out = (Stream) StatusManager.commit(subkey, new InStream(getStreamUri(body.id), body.output_stream));
            Proxy internal = (Proxy) StatusManager.commit(subkey, SourceSinkFactory.internal(out));
            internal.task((Task) StatusManager.commit(subkey, new FlushStreamTask(out)));

            //Create the query and commit it as task
            Query o = new Query(getQueryUri(body.id), body.body, out, source_streams);
            return (Query) StatusManager.commit(k, o);

        } catch (DuplicateException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public InStream get_streams(String id) {
        return (InStream) StatusManager.getStream(getStreamKey(id))
                .orElseGet(() -> StatusManager.getStream(KeyFactory.get(getQueryKey(id)))
                        .orElseThrow(() -> new ServiceException(new ResourceNotFound(id))));
    }

    @Override
    public List<InStream> get_streams() {
        Collection<Stream> values = StatusManager.streams.values();
        return values.stream().map(InStream.class::cast).collect(Collectors.toList());
    }

    @Override
    public Stream delete_stream(String id) {
        Key streamKey = getStreamKey(id);
        return deleteResource(id, streamKey, StatusManager.getStream(streamKey), Stream.class);
    }

    private Key createStreamKey(String id) {
        return KeyFactory.create(getStreamUri(id));
    }

    private Key createQueryKey(String id) {
        return KeyFactory.create(getQueryUri(id));
    }

    private String getQueryUri(String id) {
        return base + URIUtils.SLASH + "queries" + URIUtils.SLASH + id;
    }

    private Key getStreamKey(String id) {
        return KeyFactory.get(getStreamUri(id));
    }

    private String getStreamUri(String id) {
        return base + URIUtils.SLASH + "streams" + URIUtils.SLASH + id;
    }

    private Key getQueryKey(String id) {
        return KeyFactory.get(getQueryUri(id));
    }

    @Override
    public Query delete_query(String id) {
        Key streamKey = getQueryKey(id);
        return deleteResource(id, streamKey, StatusManager.getTask(streamKey), Query.class);

    }

    private <T> T deleteResource(String id, Key streamKey, Optional<?> task1, Class<T> c) {
        return task1.map(stream -> {
                    try {
                        StatusManager.remove(streamKey);
                        for (int i = 0; i < streamKey.hl(); i++) {
                            Key key = KeyFactory.get(streamKey);
                            if (key != null && i == key.hl()) {
                                StatusManager.remove(key);
                                KeyFactory.remove(streamKey);
                            }
                        }
                        KeyFactory.remove(id);
                        return stream;
                    } catch (ResourceNotFound | KeyException e) {
                        throw new ServiceException(e);
                    }
                }
        ).map(c::cast).orElseThrow(() -> new ServiceException(new ResourceNotFound(id)));
    }
}
