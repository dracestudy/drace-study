// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class Lock implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public Lock() {
  }

  /**
   * Construct from a JSON string.
   */
  public Lock(String s) throws JSONException {
    this(new JSONObject(s));
  }

  Lock(JSONObject jo) throws JSONException {
    id = jo.getString("id");
    locked_at = new Frame(jo.getJSONObject("locked_at"));

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (id != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"id\":");
      Util.writeJsonString(_out, id);
    }
    else
      throw new JSONException("Uninitialized field id");

    if (locked_at != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"locked_at\":");
      locked_at.toJsonBuffer(_out);
    }
    else
      throw new JSONException("Uninitialized field locked_at");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String id;
  public Frame locked_at;
}
