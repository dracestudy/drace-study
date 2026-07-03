// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class PointerValue implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public PointerValue() {
  }

  /**
   * Construct from a JSON string.
   */
  public PointerValue(String s) throws JSONException {
    this(new JSONObject(s));
  }

  PointerValue(JSONObject jo) throws JSONException {
    base = new BaseId(jo.get("base"));
    if (jo.has("duration")) {
      duration = new StorageDuration(jo.opt("duration"));
    }
    if (jo.has("offset")) {
      offset = jo.optInt("offset");
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (base != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"base\":");
      base.toJsonBuffer(_out);
    }
    else
      throw new JSONException("Uninitialized field base");

    if (duration != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"duration\":");
      duration.toJsonBuffer(_out);
    }

    if (offset != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"offset\":");
      _out.append(String.valueOf(offset));
    }

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public BaseId base;
  public StorageDuration duration;
  public Integer offset;
}
