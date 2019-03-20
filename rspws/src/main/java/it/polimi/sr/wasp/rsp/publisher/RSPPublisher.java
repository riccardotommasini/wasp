package it.polimi.sr.wasp.rsp.publisher;

import it.polimi.rsp.vocals.core.annotations.services.PublishingService;
import it.polimi.sr.wasp.rsp.RSPActor;
import it.polimi.sr.wasp.rsp.features.streams.StreamDeletionFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamFullRegistrationFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamGetterFeature;
import it.polimi.sr.wasp.rsp.features.streams.StreamsGetterFeature;
import it.polimi.sr.wasp.rsp.publisher.model.WebChannel;
import it.polimi.sr.wasp.rsp.publisher.syntax.Parser;
import it.polimi.sr.wasp.rsp.publisher.syntax.StreamPublicationBuilder;
import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import it.polimi.sr.wasp.server.exceptions.ServiceException;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.persist.Key;
import it.polimi.sr.wasp.server.model.persist.KeyFactory;
import it.polimi.sr.wasp.server.model.persist.StatusManager;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.Getter;
import lombok.extern.java.Log;
import org.parboiled.Parboiled;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import spark.utils.StringUtils;

import java.security.KeyException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Getter
@PublishingService(host = "localhost", port = 8181)
public abstract class RSPPublisher extends RSPActor implements StreamFullRegistrationFeature, StreamGetterFeature, StreamsGetterFeature, StreamDeletionFeature {

    private Parser parser = Parboiled.createParser(Parser.class);

    public RSPPublisher(String name, String base) {
        super(name, base.endsWith(URIUtils.SLASH) ? base + name : base + URIUtils.SLASH + name);
    }

    @Override
    public Channel register_stream(String input) {

        ParsingResult<StreamPublicationBuilder> result = new ReportingParseRunner(parser.Query()).run(input);

        if (result.hasErrors()) {
            List<ParseError> parseErrors = result.parseErrors;
            String reduce = parseErrors.stream().map(e ->
                    input.substring(0, e.getStartIndex()) + "|->" +
                            input.substring(e.getStartIndex(), e.getEndIndex()) + "<-|" +
                            input.substring(e.getEndIndex() + 1, input.length() - 1)
            ).reduce("", (s, s2) -> s + s2);

            throw new ServiceException("{ " +
                    "\"error\": \"parsing error\", " +
                    "\"message\": \"" + StringUtils.removeLeadingAndTrailingSlashesFrom(reduce) +
                    "\"}");
        }

        StreamPublicationBuilder.Built built = result.parseTreeRoot.getChildren().get(0).getValue().build();

        try {

            StatusManager.commit(KeyFactory.create(built.task.iri()), built.task);

            Key k = KeyFactory.create(built.channel.iri());
            StatusManager.commit(k, built.channel);

            Key k1 = KeyFactory.create(k);
            StatusManager.commit(k1, built.source);

            return built.channel;

        } catch (DuplicateException e) {
            throw new ServiceException(e);

        }

        //TODO add the stream metadata
        //TODO add the schema constraints

    }

    @Override
    public WebChannel get_stream(String id) {
        return (WebChannel) StatusManager.getChannel(getStreamKey(id))
                .orElseThrow(() -> new ServiceException(new ResourceNotFound(id)));
    }


    @Override
    public List<String> get_streams() {
        Collection<Channel> values = StatusManager.channels.values();
        return values.stream().map(WebChannel.class::cast).map(s -> "{" + "\"iri\":\"" + s.iri() + "\"" + "}").collect(Collectors.toList());
    }

    @Override
    public Channel delete_stream(String id) {
        Key streamKey = getStreamKey(id);
        return deleteResource(id, streamKey, StatusManager.getChannel(streamKey), Channel.class);
    }

    private Key getStreamKey(String id) {
        return URIUtils.isUri(id) ? KeyFactory.get(id) : KeyFactory.get(getStreamUri(id));
    }

    private String getStreamUri(String id) {
        return base + URIUtils.SLASH + "streams" + URIUtils.SLASH + id;
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
