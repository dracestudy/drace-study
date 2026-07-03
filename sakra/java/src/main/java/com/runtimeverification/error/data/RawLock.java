// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class RawLock implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public RawLock() {
  }

  /**
   * Construct from a JSON string.
   */
  public RawLock(String s) throws JSONException {
    this(new JSONObject(s));
  }

  RawLock(JSONObject jo) throws JSONException {
    id = new RawField(jo.getJSONObject("id"));
    locked_at = jo.getString("locked_at");

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
      id.toJsonBuffer(_out);
    }
    else
      throw new JSONException("Uninitialized field id");

    if (locked_at != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"locked_at\":");
      Util.writeJsonString(_out, locked_at);
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
  public RawField id;
  public String locked_at;
}
