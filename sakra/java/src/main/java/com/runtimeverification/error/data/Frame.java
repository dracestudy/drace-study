// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class Frame implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public Frame() {
  }

  /**
   * Construct from a JSON string.
   */
  public Frame(String s) throws JSONException {
    this(new JSONObject(s));
  }

  Frame(JSONObject jo) throws JSONException {
    symbol = jo.getString("symbol");
    if (jo.has("loc")) {
      loc = new Location(jo.optJSONObject("loc"));
    }
    locks = new java.util.ArrayList<Lock>();
    for (int _i = 0; _i < jo.getJSONArray("locks").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("locks").getJSONObject(_i);
      locks.add(new Lock(_tmp));
    }
    local_vars = new java.util.ArrayList<LocalVar>();
    for (int _i = 0; _i < jo.getJSONArray("local_vars").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("local_vars").getJSONObject(_i);
      local_vars.add(new LocalVar(_tmp));
    }
    elided = jo.getBoolean("elided");

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (symbol != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"symbol\":");
      Util.writeJsonString(_out, symbol);
    }
    else
      throw new JSONException("Uninitialized field symbol");

    if (loc != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"loc\":");
      loc.toJsonBuffer(_out);
    }

    if (locks != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"locks\":");
      _out.append("[");
      for (int i = 0; i < locks.size(); ++i) {
        locks.get(i).toJsonBuffer(_out);
        if (i < locks.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field locks");

    if (local_vars != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"local_vars\":");
      _out.append("[");
      for (int i = 0; i < local_vars.size(); ++i) {
        local_vars.get(i).toJsonBuffer(_out);
        if (i < local_vars.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field local_vars");

    if (elided != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"elided\":");
      _out.append(String.valueOf(elided));
    }
    else
      throw new JSONException("Uninitialized field elided");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String symbol;
  public Location loc;
  public java.util.ArrayList<Lock> locks;
  public java.util.ArrayList<LocalVar> local_vars;
  public Boolean elided;
}
