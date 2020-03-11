package umm3601;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;

public class ISODateParser {

  public static Date parseISO(String isoString) {
    OffsetDateTime odt = OffsetDateTime.parse(isoString);
    Instant inst = odt.toInstant();
    return Date.from(inst);
  }

}
