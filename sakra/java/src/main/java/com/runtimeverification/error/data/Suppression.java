// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class Suppression implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public Suppression() {
  }

  /**
   * Construct from a JSON string.
   */
  public Suppression(String s) throws JSONException {
    this(new JSONObject(s));
  }

  Suppression(JSONObject jo) throws JSONException {
    condition = new Condition(jo.get("condition"));
    suppress = jo.getBoolean("suppress");

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (condition != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"condition\":");
      condition.toJsonBuffer(_out);
    }
    else
      throw new JSONException("Uninitialized field condition");

    if (suppress != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"suppress\":");
      _out.append(String.valueOf(suppress));
    }
    else
      throw new JSONException("Uninitialized field suppress");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public Condition condition;
  public Boolean suppress;
}
