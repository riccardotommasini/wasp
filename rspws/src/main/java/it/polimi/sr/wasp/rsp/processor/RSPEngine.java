package it.polimi.sr.wasp.rsp.processor;

import it.polimi.rsp.vocals.core.annotations.VocalsFactory;
import it.polimi.rsp.vocals.core.annotations.VocalsStreamStub;
import it.polimi.rsp.vocals.core.annotations.services.ProcessingService;
import it.polimi.sr.wasp.rsp.RSPActor;
import it.polimi.sr.wasp.rsp.exceptions.InternalEngineException;
import it.polimi.sr.wasp.rsp.features.queries.QueriesGetterFeature;
import it.polimi.sr.wasp.rsp.features.queries.QueryDeletionFeature;
import it.polimi.sr.wasp.rsp.features.queries.QueryGetterFeature;
import it.polimi.sr.wasp.rsp.features.queries.QueryRegistrationFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamDeletionFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamGetterFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamRegistrationFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamsGetterFeature;
import it.polimi.sr.wasp.rsp.model.InternalTaskWrapper;
import it.polimi.sr.wasp.rsp.model.StatelessDataChannel;
import it.polimi.sr.wasp.rsp.model.TaskBody;
import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import it.polimi.sr.wasp.server.exceptions.ServiceException;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Source;
import it.polimi.sr.wasp.server.model.concept.tasks.Task;
import it.polimi.sr.wasp.server.model.persist.Key;
import it.polimi.sr.wasp.server.model.persist.KeyFactory;
import it.polimi.sr.wasp.server.model.persist.StatusManager;
import it.polimi.sr.wasp.server.web.WebSocketSource;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.extern.java.Log;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.polimi.sr.wasp.utils.URIUtils.getQueryUri;
import static it.polimi.sr.wasp.utils.URIUtils.getStreamUri;

@Log
@ProcessingService(host = "localhost", port = 8181)
public abstract class RSPEngine extends RSPActor implements QueryRegistrationFeature, QueryDeletionFeature, QueryGetterFeature, QueriesGetterFeature, StreamRegistrationFeature, StreamGetterFeature, StreamsGetterFeature, StreamDeletionFeature {
    public RSPEngine(String name, String base) {
        super(name, base.endsWith(URIUtils.SLASH) ? base + name : base + URIUtils.SLASH + name);
    }

    @Override
    public InternalTaskWrapper register_query(TaskBody body) {
        try {

            List<Channel> streams = new ArrayList<>();
            String[] input_streams = extractStreams(body);

            for (String s : input_streams) {
                Channel channel = StatusManager
                        .getChannel(getStreamKey(s))
                        .orElseGet(() -> register_vocals_stream(s));
                streams.add(channel);
                //TODO commit channel again?
            }

            Key k = createQueryKey(body.id);

            Task query = handleInternalQuery(body, streams);

            Channel out = query.out();

            StatusManager.commit(KeyFactory.create(k), out);

            //Create the query and commit it as add
            return (InternalTaskWrapper) StatusManager.commit(k, query);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e);
        }
    }

    protected abstract String[] extractStreams(TaskBody body);

    @Override
    public List<String> get_queries() {
        return StatusManager.tasks.values().stream().filter(task -> task instanceof InternalTaskWrapper)
                .map(InternalTaskWrapper.class::cast).map(q -> "{" + "\"iri\":\"" + q.iri() + "\"" + "}").collect(Collectors.toList());
    }

    @Override
    public InternalTaskWrapper get_query(String id) {
        return (InternalTaskWrapper) StatusManager.getTask(getQueryKey(id))
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
        } catch (DuplicateException | InternalEngineException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Channel register_stream(String id, String uri_source) {
        String iri = getStreamUri(base, URIUtils.cleanProtocols(id));
        return _register_stream(iri, uri_source);
    }

    public Channel register_vocals_stream(String url) {
        log.info("Accessing VoCaLS Description at [" + url + "]");
        try {
            VocalsStreamStub fetch = VocalsFactory.get().fetch(url);
            if (fetch != null || fetch.uri != null || fetch.source != null) {
                return _register_stream(url, fetch.source);
            } else throw new Exception("Not Valid Stream [" + url + "]");
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public StatelessDataChannel get_stream(String id) {
        return (StatelessDataChannel) StatusManager.getChannel(getStreamKey(id))
                .orElseGet(() -> StatusManager.getChannel(KeyFactory.get(getQueryKey(id)))
                        .orElseThrow(() -> new ServiceException(new ResourceNotFound(id))));
    }

    @Override
    public List<String> get_streams() {
        Collection<Channel> values = StatusManager.channels.values();
        return values.stream().map(StatelessDataChannel.class::cast).map(s -> "{" + "\"iri\":\"" + s.iri() + "\"" + "}").collect(Collectors.toList());
    }

    @Override
    public Channel delete_stream(String id) {
        Key streamKey = getStreamKey(id);
        return deleteResource(id, streamKey, StatusManager.getChannel(streamKey), Channel.class);
    }

    private Key createQueryKey(String id) {
        return KeyFactory.create(getQueryUri(base, id));
    }

    private Key getStreamKey(String id) {
        return URIUtils.isUri(id) ? KeyFactory.get(id) : KeyFactory.get(getStreamUri(base, id));
    }

    private Key getQueryKey(String id) {
        return KeyFactory.get(getQueryUri(base, id));
    }

    @Override
    public InternalTaskWrapper delete_query(String id) {
        Key streamKey = getQueryKey(id);
        return deleteResource(id, streamKey, StatusManager.getTask(streamKey), InternalTaskWrapper.class);

    }

    private <T> T deleteResource(String id, Key streamKey, Optional<?> task1, Class<T> c) {
        return task1.map(stream -> {
                    try {
                        StatusManager.remove(streamKey);
                        return stream;
                    } catch (ResourceNotFound | KeyException e) {
                        throw new ServiceException(e);
                    }
                }
        ).map(c::cast).orElseThrow(() -> new ServiceException(new ResourceNotFound(id)));
    }


    protected abstract InternalTaskWrapper handleInternalQuery(TaskBody body, List<Channel> input_streams) throws InternalEngineException;

    protected abstract Channel handleInternalStream(String id, String body) throws InternalEngineException;

}
