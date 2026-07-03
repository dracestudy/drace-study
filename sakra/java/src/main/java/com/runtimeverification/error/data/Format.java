// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

/**
 * Construct objects of type format.
 */

public class Format {
  Tag t = null;

  public Format() {
  }

  public Tag tag() {
    return t;
  }

  /**
   * Define tags for sum type format.
   */
  public enum Tag {
    CSV, CONSOLE, HTML, JSON
  }

  public Format(Object o) throws JSONException {
    String tag = Util.extractTag(o);
    if (tag.equals("CSV"))
      t = Tag.CSV;
    else if (tag.equals("Console"))
      t = Tag.CONSOLE;
    else if (tag.equals("HTML"))
      t = Tag.HTML;
    else if (tag.equals("JSON"))
      t = Tag.JSON;
    else
      throw new JSONException("Invalid tag: " + tag);
  }

  public void setCSV() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.CSV;
  }

  public void setConsole() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.CONSOLE;
  }

  public void setHTML() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.HTML;
  }

  public void setJSON() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.JSON;
  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    if (t == null)
      throw new JSONException("Uninitialized Format");
    else {
      switch(t) {
      case CSV:
        _out.append("\"CSV\"");
        break;
      case CONSOLE:
        _out.append("\"Console\"");
        break;
      case HTML:
        _out.append("\"HTML\"");
        break;
      case JSON:
        _out.append("\"JSON\"");
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
