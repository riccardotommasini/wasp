package it.polimi.sr.wasp.rsp;

import it.polimi.rsp.vocals.core.annotations.services.ProcessingService;
import it.polimi.sr.wasp.rsp.features.queries.QueriesGetterFeature;
import it.polimi.sr.wasp.rsp.features.queries.QueryDeletionFeature;
import it.polimi.sr.wasp.rsp.features.queries.QueryGetterFeature;
import it.polimi.sr.wasp.rsp.features.queries.QueryRegistrationFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamDeletionFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamGetterFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamRegistrationFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamsGetterFeature;
import it.polimi.sr.wasp.rsp.model.InStream;
import it.polimi.sr.wasp.rsp.model.Query;
import it.polimi.sr.wasp.rsp.model.QueryBody;
import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import it.polimi.sr.wasp.server.exceptions.ServiceException;
import it.polimi.sr.wasp.server.model.Key;
import it.polimi.sr.wasp.server.model.KeyFactory;
import it.polimi.sr.wasp.server.model.StatusManager;
import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.server.web.*;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.Getter;
import lombok.extern.java.Log;

import java.security.KeyException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Getter
@ProcessingService(host = "localhost", port = 8181)
public abstract class RSPEngine implements QueryRegistrationFeature, QueryDeletionFeature, QueryGetterFeature, QueriesGetterFeature, StreamRegistrationFeature, StreamGetterFeature, StreamsGetterFeature, StreamDeletionFeature {

    private final String base;
    private final String name;

    public RSPEngine(String name, String base) {
        this.name = name;
        this.base = base;
    }

    @Override
    public Query register_query(QueryBody body) {
        try {

            Key k = createQueryKey(body.id);
            Key subkey = KeyFactory.create(k);

            //Retrieve the streams and create if unavailable, need vocals description
            for (String s : body.input_streams) {
                StatusManager
                        .getStream(getStreamKey(s))
                        .orElseGet(() -> register_stream(s, s));
            }

            Query query = handleInternalQuery(getQueryUri(body.id), body.body);

            //Since we want to consumer the output of this query as a stream; We attach an internal sink and we connect it
            // using a proxy

            Stream out = new InStream(getStreamUri(body.id), body.output_stream);

            //These can be deleted using subkey as key

            //Sink formatter1 = query.sink();
            // Source formatter2 = query.source();
            Proxy proxy = query.proxy();

            FeedStreamTask feedtask = new FeedStreamTask(out, proxy);
            FlushStreamTask flushtask = new FlushStreamTask(out, proxy);

            StatusManager.commit(subkey, proxy, feedtask, flushtask, out);

            //Create the query and commit it as task
            return (Query) StatusManager.commit(k, query);

        } catch (DuplicateException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Query> get_queries() {
        return StatusManager.tasks.values().stream()
                .filter(task -> task instanceof Query)
                .map(Query.class::cast).collect(Collectors.toList());
    }

    @Override
    public Query get_query(String id) {
        return (Query) StatusManager.getTask(getQueryKey(id))
                .orElseThrow(() -> new ServiceException(new ResourceNotFound(id)));
    }

    @Override
    public Stream register_stream(String id1, String cc) {
        try {
            String id = URIUtils.cleanProtocols(id1);
            Stream stream = handleInternalStream(id, cc);
            Source source = SourceSinkFactory.webSocket(stream);
            FeedStreamTask task = new FeedStreamTask(stream, source);

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
    public InStream get_stream(String id) {
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

    protected abstract Query handleInternalQuery(String queryUri, String body);

    protected abstract Stream handleInternalStream(String id, String body);

}
