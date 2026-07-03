// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class FieldsInfo implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public FieldsInfo() {
  }

  /**
   * Construct from a JSON string.
   */
  public FieldsInfo(String s) throws JSONException {
    this(new JSONObject(s));
  }

  FieldsInfo(JSONObject jo) throws JSONException {
    fields = new java.util.ArrayList<FieldValue>();
    for (int _i = 0; _i < jo.getJSONArray("fields").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("fields").getJSONObject(_i);
      fields.add(new FieldValue(_tmp));
    }
    paddings = new java.util.ArrayList<Padding>();
    for (int _i = 0; _i < jo.getJSONArray("paddings").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("paddings").getJSONObject(_i);
      paddings.add(new Padding(_tmp));
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (fields != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"fields\":");
      _out.append("[");
      for (int i = 0; i < fields.size(); ++i) {
        fields.get(i).toJsonBuffer(_out);
        if (i < fields.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field fields");

    if (paddings != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"paddings\":");
      _out.append("[");
      for (int i = 0; i < paddings.size(); ++i) {
        paddings.get(i).toJsonBuffer(_out);
        if (i < paddings.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field paddings");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public java.util.ArrayList<FieldValue> fields;
  public java.util.ArrayList<Padding> paddings;
}
