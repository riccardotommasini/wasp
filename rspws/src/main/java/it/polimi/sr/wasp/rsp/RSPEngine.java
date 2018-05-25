package it.polimi.sr.wasp.rsp;

import it.polimi.rsp.vocals.core.annotations.VocalsFactory;
import it.polimi.rsp.vocals.core.annotations.VocalsStreamStub;
import it.polimi.rsp.vocals.core.annotations.services.ProcessingService;
import it.polimi.sr.wasp.rsp.features.queries.QueriesGetterFeature;
import it.polimi.sr.wasp.rsp.features.queries.QueryDeletionFeature;
import it.polimi.sr.wasp.rsp.features.queries.QueryGetterFeature;
import it.polimi.sr.wasp.rsp.features.queries.QueryRegistrationFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamDeletionFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamGetterFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamRegistrationFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamsGetterFeature;
import it.polimi.sr.wasp.rsp.model.Query;
import it.polimi.sr.wasp.rsp.model.QueryBody;
import it.polimi.sr.wasp.rsp.model.Stream;
import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import it.polimi.sr.wasp.server.exceptions.ServiceException;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Source;
import it.polimi.sr.wasp.server.model.persist.Key;
import it.polimi.sr.wasp.server.model.persist.KeyFactory;
import it.polimi.sr.wasp.server.model.persist.StatusManager;
import it.polimi.sr.wasp.server.web.WebSocketSource;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.Getter;
import lombok.extern.java.Log;

import java.security.KeyException;
import java.util.ArrayList;
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
        this.base = base.endsWith(URIUtils.SLASH) ? base + name : base + URIUtils.SLASH + name;
    }

    @Override
    public Query register_query(QueryBody body) {
        try {

            List<Channel> streams = new ArrayList<>();
            for (String s : body.input_streams) {
                Channel channel = StatusManager
                        .getStream(getStreamKey(s))
                        .orElseGet(() -> register_vocals_stream(s, s));
                streams.add(channel);
                //TODO commit channel again?
            }

            Key k = createQueryKey(body.id);

            Query query = handleInternalQuery(getQueryUri(body.id), body.body, getStreamUri(body.id), body.output_stream);

            for (Channel channel : streams) {
                channel.apply(query);
                query.add(channel);
            }


            Channel out = query.out();

            StatusManager.commit(KeyFactory.create(k), out);

            //Create the query and commit it as add
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

    private Channel _register_stream(String iri, String uri_source) {
        try {
            Source source = new WebSocketSource(uri_source);
            Channel channel = handleInternalStream(iri, uri_source);

            source.add(channel);

            //TODO source creation structured with vocals
            Key k = KeyFactory.create(iri);
            StatusManager.commit(k, channel);
            Key k1 = KeyFactory.create(k);
            StatusManager.commit(k1, source);

            return channel;
        } catch (DuplicateException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Channel register_stream(String id, String uri_source) {
        String iri = getStreamUri(URIUtils.cleanProtocols(id));
        return _register_stream(iri, uri_source);
    }

    public Channel register_vocals_stream(String s, String s1) {
        VocalsStreamStub fetch = VocalsFactory.get().fetch(s1);
        return _register_stream(fetch.uri, fetch.source);
    }

    @Override
    public Stream get_stream(String id) {
        return (Stream) StatusManager.getStream(getStreamKey(id))
                .orElseGet(() -> StatusManager.getStream(KeyFactory.get(getQueryKey(id)))
                        .orElseThrow(() -> new ServiceException(new ResourceNotFound(id))));
    }

    @Override
    public List<Stream> get_streams() {
        Collection<Channel> values = StatusManager.streams.values();
        return values.stream().map(Stream.class::cast).collect(Collectors.toList());
    }

    @Override
    public Channel delete_stream(String id) {
        Key streamKey = getStreamKey(id);
        return deleteResource(id, streamKey, StatusManager.getStream(streamKey), Channel.class);
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

    protected abstract Query handleInternalQuery(String queryUri, String body, String stream_uri, String stream_source);

    protected abstract Channel handleInternalStream(String id, String body);

}
