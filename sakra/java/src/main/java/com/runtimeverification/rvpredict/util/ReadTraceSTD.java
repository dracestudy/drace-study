package com.runtimeverification.rvpredict.util;

import com.runtimeverification.rvpredict.config.Configuration;
import com.runtimeverification.rvpredict.log.ReadonlyEventInterface;
import com.runtimeverification.rvpredict.metadata.Metadata;
import com.runtimeverification.rvpredict.trace.OrderedLoggedTraceReader;
import java.io.IOException;

public class ReadTraceSTD {
  public static void main(String args[]) {
    Configuration config = Configuration.instance(args);
    Metadata metadata = Metadata.readFrom(config.getMetadataPath());
    try (OrderedLoggedTraceReader reader = new OrderedLoggedTraceReader(config)) {
      while (true) {
        ReadonlyEventInterface event = reader.readEvent();
        if (event.isMetaEvent()) continue;
        // String locSig = event.getLocationId() < 0 ?
        //         "n/a" :
        //         metadata.getLocationSig(event.getLocationId());
        // System.out.printf("%s %s%n", event.toString(), locSig);
        System.out.println(toStdFromat(event));
      }
    } catch (IOException e) {
    }
  }

  public static String toStdFromat(ReadonlyEventInterface event) {
    // 23|acq(1107831784)|633
    // 23|r(4294967095)|644
    // 23|w(4294967095)|644
    // 23|rel(1107831784)|633
    // 1|join(23)|624
    // 1|r(4294967095)|629
    // 24|fork(5)|646
    String eventType = "";
    switch (event.getType()) {
      case READ:
        eventType = "r";
        break;
      case WRITE:
        eventType = "w";
        break;
      case WRITE_LOCK:
      case READ_LOCK:
        eventType = "acq";
        break;
      case WRITE_UNLOCK:
      case READ_UNLOCK:
        eventType = "rel";
        break;
      case START_THREAD:
        eventType = "fork";
        break;
      case JOIN_THREAD:
        eventType = "join";
        break;
        // case WAIT_ACQUIRE:
        // case WAIT_RELEASE:
      default:
        break;
    }

    return String.format(
        "%s|%s(%s)|%s|%s",
        event.getOriginalThreadId(),
        eventType,
        // Long.toHexString(event.unsafeGetDataInternalIdentifier()),
        event.unsafeGetDataInternalIdentifier(),
        event.getLocationId(),
        event.unsafeGetDataValue());
    // return String.format("(%s, E%s, T%s, D%s, L%s, %s, %s)",
    //         type, eventId, originalThreadId, signalDepth, locationId, addr,
    //         Long.toHexString(dataValue));
  }
}
