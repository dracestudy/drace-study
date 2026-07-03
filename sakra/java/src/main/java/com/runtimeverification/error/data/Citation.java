// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class Citation implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public Citation() {
  }

  /**
   * Construct from a JSON string.
   */
  public Citation(String s) throws JSONException {
    this(new JSONObject(s));
  }

  Citation(JSONObject jo) throws JSONException {
    document = jo.getString("document");
    section = jo.getString("section");
    if (jo.has("paragraph")) {
      paragraph = jo.optString("paragraph");
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (document != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"document\":");
      Util.writeJsonString(_out, document);
    }
    else
      throw new JSONException("Uninitialized field document");

    if (section != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"section\":");
      Util.writeJsonString(_out, section);
    }
    else
      throw new JSONException("Uninitialized field section");

    if (paragraph != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"paragraph\":");
      Util.writeJsonString(_out, paragraph);
    }

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String document;
  public String section;
  public String paragraph;
}
