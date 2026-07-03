// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class RawFieldFrame implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public RawFieldFrame() {
  }

  /**
   * Construct from a JSON string.
   */
  public RawFieldFrame(String s) throws JSONException {
    this(new JSONObject(s));
  }

  RawFieldFrame(JSONObject jo) throws JSONException {
    pc = jo.getString("pc");
    cfa = jo.getString("cfa");

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (pc != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"pc\":");
      Util.writeJsonString(_out, pc);
    }
    else
      throw new JSONException("Uninitialized field pc");

    if (cfa != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"cfa\":");
      Util.writeJsonString(_out, cfa);
    }
    else
      throw new JSONException("Uninitialized field cfa");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String pc;
  public String cfa;
}
