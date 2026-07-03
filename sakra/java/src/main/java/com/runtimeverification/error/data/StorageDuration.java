// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

/**
 * Construct objects of type storage_duration.
 */

public class StorageDuration {
  Tag t = null;

  public StorageDuration() {
  }

  public Tag tag() {
    return t;
  }

  /**
   * Define tags for sum type storage_duration.
   */
  public enum Tag {
    ALLOCATED, STATIC_, THREAD, AUTO, UNKNOWN
  }

  public StorageDuration(Object o) throws JSONException {
    String tag = Util.extractTag(o);
    if (tag.equals("Allocated"))
      t = Tag.ALLOCATED;
    else if (tag.equals("Static"))
      t = Tag.STATIC_;
    else if (tag.equals("Thread"))
      t = Tag.THREAD;
    else if (tag.equals("Auto"))
      t = Tag.AUTO;
    else if (tag.equals("Unknown")) {
      field_unknown = ((JSONArray)o).getString(1);

      t = Tag.UNKNOWN;
    }
    else
      throw new JSONException("Invalid tag: " + tag);
  }

  public void setAllocated() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.ALLOCATED;
  }

  public void setStatic() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.STATIC_;
  }

  public void setThread() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.THREAD;
  }

  public void setAuto() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.AUTO;
  }

  String field_unknown = null;
  public void setUnknown(String x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.UNKNOWN;
    field_unknown = x;
  }
  public String getUnknown() {
    if (t == Tag.UNKNOWN)
      return field_unknown;
    else
      return null;
  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    if (t == null)
      throw new JSONException("Uninitialized StorageDuration");
    else {
      switch(t) {
      case ALLOCATED:
        _out.append("\"Allocated\"");
        break;
      case STATIC_:
        _out.append("\"Static\"");
        break;
      case THREAD:
        _out.append("\"Thread\"");
        break;
      case AUTO:
        _out.append("\"Auto\"");
        break;
      case UNKNOWN:
         _out.append("[\"Unknown\",");
         Util.writeJsonString(_out, field_unknown);
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
