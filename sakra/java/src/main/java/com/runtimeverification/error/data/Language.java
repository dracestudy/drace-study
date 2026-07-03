// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

/**
 * Construct objects of type language.
 */

public class Language {
  Tag t = null;

  public Language() {
  }

  public Tag tag() {
    return t;
  }

  /**
   * Define tags for sum type language.
   */
  public enum Tag {
    C, CPP
  }

  public Language(Object o) throws JSONException {
    String tag = Util.extractTag(o);
    if (tag.equals("C"))
      t = Tag.C;
    else if (tag.equals("CPP"))
      t = Tag.CPP;
    else
      throw new JSONException("Invalid tag: " + tag);
  }

  public void setC() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.C;
  }

  public void setCPP() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.CPP;
  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    if (t == null)
      throw new JSONException("Uninitialized Language");
    else {
      switch(t) {
      case C:
        _out.append("\"C\"");
        break;
      case CPP:
        _out.append("\"CPP\"");
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
