// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class RawField implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public RawField() {
  }

  /**
   * Construct from a JSON string.
   */
  public RawField(String s) throws JSONException {
    this(new JSONObject(s));
  }

  RawField(JSONObject jo) throws JSONException {
    address = jo.getString("address");
    if (jo.has("frame1")) {
      frame1 = new RawFieldFrame(jo.optJSONObject("frame1"));
    }
    if (jo.has("frame2")) {
      frame2 = new RawFieldFrame(jo.optJSONObject("frame2"));
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

    if (frame1 != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"frame1\":");
      frame1.toJsonBuffer(_out);
    }

    if (frame2 != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"frame2\":");
      frame2.toJsonBuffer(_out);
    }

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String address;
  public RawFieldFrame frame1;
  public RawFieldFrame frame2;
}
