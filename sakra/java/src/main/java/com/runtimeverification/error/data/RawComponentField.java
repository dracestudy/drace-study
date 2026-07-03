// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

/**
 * Construct objects of type raw_component_field.
 */

public class RawComponentField {
  Tag t = null;

  public RawComponentField() {
  }

  public Tag tag() {
    return t;
  }

  /**
   * Define tags for sum type raw_component_field.
   */
  public enum Tag {
    SIGNAL, LOCK
  }

  public RawComponentField(Object o) throws JSONException {
    String tag = Util.extractTag(o);
    if (tag.equals("Signal")) {
      field_signal = ((JSONArray)o).getInt(1);

      t = Tag.SIGNAL;
    }
    else if (tag.equals("Lock")) {
      field_lock = new RawField(((JSONArray)o).getJSONObject(1));

      t = Tag.LOCK;
    }
    else
      throw new JSONException("Invalid tag: " + tag);
  }

  Integer field_signal = null;
  public void setSignal(Integer x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.SIGNAL;
    field_signal = x;
  }
  public Integer getSignal() {
    if (t == Tag.SIGNAL)
      return field_signal;
    else
      return null;
  }

  RawField field_lock = null;
  public void setLock(RawField x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.LOCK;
    field_lock = x;
  }
  public RawField getLock() {
    if (t == Tag.LOCK)
      return field_lock;
    else
      return null;
  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    if (t == null)
      throw new JSONException("Uninitialized RawComponentField");
    else {
      switch(t) {
      case SIGNAL:
         _out.append("[\"Signal\",");
         _out.append(String.valueOf(field_signal));
         _out.append("]");
         break;
      case LOCK:
         _out.append("[\"Lock\",");
         field_lock.toJsonBuffer(_out);
         _out.append("]");
         break;
      default:
        break; /* unused; keeps compiler happy */
      }
    }
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
}
