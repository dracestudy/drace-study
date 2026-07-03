// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

/**
 * Construct objects of type byte_value.
 */

public class ByteValue {
  Tag t = null;

  public ByteValue() {
  }

  public Tag tag() {
    return t;
  }

  /**
   * Define tags for sum type byte_value.
   */
  public enum Tag {
    INT_, INDETERMINATE, UNSPECIFIED, BITFIELD, ENCODEDVALUE
  }

  public ByteValue(Object o) throws JSONException {
    String tag = Util.extractTag(o);
    if (tag.equals("Int")) {
      field_int_ = ((JSONArray)o).getInt(1);

      t = Tag.INT_;
    }
    else if (tag.equals("Indeterminate"))
      t = Tag.INDETERMINATE;
    else if (tag.equals("Unspecified"))
      t = Tag.UNSPECIFIED;
    else if (tag.equals("Bitfield")) {
      field_bitfield = new java.util.ArrayList<SizedByteValue>();
      for (int _i = 0; _i < ((JSONArray)o).getJSONArray(1).length(); ++_i) {
        JSONObject _tmp = ((JSONArray)o).getJSONArray(1).getJSONObject(_i);
        field_bitfield.add(new SizedByteValue(_tmp));
      }

      t = Tag.BITFIELD;
    }
    else if (tag.equals("EncodedValue")) {
      field_encodedvalue = new EncodedValue(((JSONArray)o).getJSONObject(1));

      t = Tag.ENCODEDVALUE;
    }
    else
      throw new JSONException("Invalid tag: " + tag);
  }

  Integer field_int_ = null;
  public void setInt(Integer x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.INT_;
    field_int_ = x;
  }
  public Integer getInt() {
    if (t == Tag.INT_)
      return field_int_;
    else
      return null;
  }

  public void setIndeterminate() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.INDETERMINATE;
  }

  public void setUnspecified() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.UNSPECIFIED;
  }

  java.util.ArrayList<SizedByteValue> field_bitfield = null;
  public void setBitfield(java.util.ArrayList<SizedByteValue> x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.BITFIELD;
    field_bitfield = x;
  }
  public java.util.ArrayList<SizedByteValue> getBitfield() {
    if (t == Tag.BITFIELD)
      return field_bitfield;
    else
      return null;
  }

  EncodedValue field_encodedvalue = null;
  public void setEncodedValue(EncodedValue x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.ENCODEDVALUE;
    field_encodedvalue = x;
  }
  public EncodedValue getEncodedValue() {
    if (t == Tag.ENCODEDVALUE)
      return field_encodedvalue;
    else
      return null;
  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    if (t == null)
      throw new JSONException("Uninitialized ByteValue");
    else {
      switch(t) {
      case INT_:
         _out.append("[\"Int\",");
         _out.append(String.valueOf(field_int_));
         _out.append("]");
         break;
      case INDETERMINATE:
        _out.append("\"Indeterminate\"");
        break;
      case UNSPECIFIED:
        _out.append("\"Unspecified\"");
        break;
      case BITFIELD:
         _out.append("[\"Bitfield\",");
         _out.append("[");
         for (int i = 0; i < field_bitfield.size(); ++i) {
           field_bitfield.get(i).toJsonBuffer(_out);
           if (i < field_bitfield.size() - 1)
             _out.append(",");
         }
         _out.append("]");
         _out.append("]");
         break;
      case ENCODEDVALUE:
         _out.append("[\"EncodedValue\",");
         field_encodedvalue.toJsonBuffer(_out);
         _out.append("]");
         break;
      default:
        break; /* unused; keeps compiler happy */
      }
    }
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
}
