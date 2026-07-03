// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

/**
 * Construct objects of type base_id.
 */

public class BaseId {
  Tag t = null;

  public BaseId() {
  }

  public Tag tag() {
    return t;
  }

  /**
   * Define tags for sum type base_id.
   */
  public enum Tag {
    REFERENCE, BASE
  }

  public BaseId(Object o) throws JSONException {
    String tag = Util.extractTag(o);
    if (tag.equals("Reference")) {
      field_reference = ((JSONArray)o).getString(1);

      t = Tag.REFERENCE;
    }
    else if (tag.equals("Base")) {
      field_base = ((JSONArray)o).getInt(1);

      t = Tag.BASE;
    }
    else
      throw new JSONException("Invalid tag: " + tag);
  }

  String field_reference = null;
  public void setReference(String x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.REFERENCE;
    field_reference = x;
  }
  public String getReference() {
    if (t == Tag.REFERENCE)
      return field_reference;
    else
      return null;
  }

  Integer field_base = null;
  public void setBase(Integer x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.BASE;
    field_base = x;
  }
  public Integer getBase() {
    if (t == Tag.BASE)
      return field_base;
    else
      return null;
  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    if (t == null)
      throw new JSONException("Uninitialized BaseId");
    else {
      switch(t) {
      case REFERENCE:
         _out.append("[\"Reference\",");
         Util.writeJsonString(_out, field_reference);
         _out.append("]");
         break;
      case BASE:
         _out.append("[\"Base\",");
         _out.append(String.valueOf(field_base));
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
