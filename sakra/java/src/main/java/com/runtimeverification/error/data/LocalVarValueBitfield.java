// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class LocalVarValueBitfield implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public LocalVarValueBitfield() {
  }

  /**
   * Construct from a JSON string.
   */
  public LocalVarValueBitfield(String s) throws JSONException {
    this(new JSONObject(s));
  }

  LocalVarValueBitfield(JSONObject jo) throws JSONException {
    value = new java.util.ArrayList<SizedByteValue>();
    for (int _i = 0; _i < jo.getJSONArray("value").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("value").getJSONObject(_i);
      value.add(new SizedByteValue(_tmp));
    }
    padding = new java.util.ArrayList<SizedByteValue>();
    for (int _i = 0; _i < jo.getJSONArray("padding").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("padding").getJSONObject(_i);
      padding.add(new SizedByteValue(_tmp));
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (value != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"value\":");
      _out.append("[");
      for (int i = 0; i < value.size(); ++i) {
        value.get(i).toJsonBuffer(_out);
        if (i < value.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field value");

    if (padding != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"padding\":");
      _out.append("[");
      for (int i = 0; i < padding.size(); ++i) {
        padding.get(i).toJsonBuffer(_out);
        if (i < padding.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field padding");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public java.util.ArrayList<SizedByteValue> value;
  public java.util.ArrayList<SizedByteValue> padding;
}
