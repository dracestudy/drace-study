// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class RawFrame implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public RawFrame() {
  }

  /**
   * Construct from a JSON string.
   */
  public RawFrame(String s) throws JSONException {
    this(new JSONObject(s));
  }

  RawFrame(JSONObject jo) throws JSONException {
    address = jo.getString("address");
    locks = new java.util.ArrayList<RawLock>();
    for (int _i = 0; _i < jo.getJSONArray("locks").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("locks").getJSONObject(_i);
      locks.add(new RawLock(_tmp));
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (address != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"address\":");
      Util.writeJsonString(_out, address);
    }
    else
      throw new JSONException("Uninitialized field address");

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

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String address;
  public java.util.ArrayList<RawLock> locks;
}
