package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.ais.messages.Metadata;
import eu.cise.adaptor.AISNormalizer;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.NavigationStatus;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.FALSE;

/**
 * This classes normalize the AISMessage class read by the tbsalling's library
 * into an internal one.
 *
 * The message is translated field by field in order to support many different
 * AIS libraries.
 *
 * The timestamp sometimes is not filled in the source AISMessage object and in
 * this case the timestamp field is filled with Instant.MIN value.
 *
 * @return an AISMsg object
 */
public class DefaultAISNormalizer implements AISNormalizer {

    public AISMsg normalize(AISMessage m) {
        Integer type = m.getMessageType().getCode();
        AISMsg.Builder b = new AISMsg.Builder(type);

        // TODO the remaining fields are not supported by other type of messages
        // than message type 1,2,3
        if (isPositionMessage(type))
            return b.build();

        b.withMMSI(m.getSourceMmsi().getMMSI());
        b.withLatitude((Float) m.dataFields().getOrDefault("latitude", 0F));
        b.withLongitude((Float) m.dataFields().getOrDefault("longitude", 0F));
        b.withPositionAccuracy(getPositionAccuracy(m.dataFields()));
        b.withCOG((Float) m.dataFields().getOrDefault("courseOverGround", 0F));
        b.withSOG((Float) m.dataFields().getOrDefault("speedOverGround", 0F));
        b.withTrueHeading((Integer) m.dataFields().getOrDefault("trueHeading", 0));
        b.withNavigationStatus(NavigationStatus.valueOf((String) m.dataFields().get("navigationStatus")));
        b.withTimestamp(oMeta(m).map(Metadata::getReceived).orElse(Instant.MIN));
        return b.build();
    }


    /**
     * @return 1 if position accuracy lte 10m; 0 otherwise.
     */
    private int getPositionAccuracy(Map<String, Object> m) {
        return (Boolean) m.getOrDefault("positionAccuracy", FALSE) ? 1 : 0;
    }

    private boolean isPositionMessage(Integer type) {
        return type != 1 && type != 2 && type != 3;
    }

    private Optional<Metadata> oMeta(AISMessage m) {
        return Optional.ofNullable(m.getMetadata());
    }

}