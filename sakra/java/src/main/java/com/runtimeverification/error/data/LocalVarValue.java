// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

/**
 * Construct objects of type local_var_value.
 */

public class LocalVarValue {
  Tag t = null;

  public LocalVarValue() {
  }

  public Tag tag() {
    return t;
  }

  /**
   * Define tags for sum type local_var_value.
   */
  public enum Tag {
    BYTES, INT_, FLOAT_, POINTER, NULLPOINTER, FILEDESCRIPTOR, FUNCTION, ARRAY, STRUCT, UNION, ENUMCONSTANT, BITFIELD
  }

  public LocalVarValue(Object o) throws JSONException {
    String tag = Util.extractTag(o);
    if (tag.equals("Bytes")) {
      field_bytes = new java.util.ArrayList<SizedByteValue>();
      for (int _i = 0; _i < ((JSONArray)o).getJSONArray(1).length(); ++_i) {
        JSONObject _tmp = ((JSONArray)o).getJSONArray(1).getJSONObject(_i);
        field_bytes.add(new SizedByteValue(_tmp));
      }

      t = Tag.BYTES;
    }
    else if (tag.equals("Int")) {
      field_int_ = ((JSONArray)o).getInt(1);

      t = Tag.INT_;
    }
    else if (tag.equals("Float")) {
      field_float_ = ((JSONArray)o).getString(1);

      t = Tag.FLOAT_;
    }
    else if (tag.equals("Pointer")) {
      field_pointer = new PointerValue(((JSONArray)o).getJSONObject(1));

      t = Tag.POINTER;
    }
    else if (tag.equals("NullPointer"))
      t = Tag.NULLPOINTER;
    else if (tag.equals("FileDescriptor")) {
      field_filedescriptor = ((JSONArray)o).getString(1);

      t = Tag.FILEDESCRIPTOR;
    }
    else if (tag.equals("Function"))
      t = Tag.FUNCTION;
    else if (tag.equals("Array")) {
      field_array = new java.util.ArrayList<LocalVarValue>();
      for (int _i = 0; _i < ((JSONArray)o).getJSONArray(1).length(); ++_i) {
        Object _tmp = ((JSONArray)o).getJSONArray(1).get(_i);
        field_array.add(new LocalVarValue(_tmp));
      }

      t = Tag.ARRAY;
    }
    else if (tag.equals("Struct")) {
      field_struct = new FieldsInfo(((JSONArray)o).getJSONObject(1));

      t = Tag.STRUCT;
    }
    else if (tag.equals("Union")) {
      field_union = new FieldsInfo(((JSONArray)o).getJSONObject(1));

      t = Tag.UNION;
    }
    else if (tag.equals("EnumConstant")) {
      field_enumconstant = ((JSONArray)o).getString(1);

      t = Tag.ENUMCONSTANT;
    }
    else if (tag.equals("Bitfield")) {
      field_bitfield = new LocalVarValueBitfield(((JSONArray)o).getJSONObject(1));

      t = Tag.BITFIELD;
    }
    else
      throw new JSONException("Invalid tag: " + tag);
  }

  java.util.ArrayList<SizedByteValue> field_bytes = null;
  public void setBytes(java.util.ArrayList<SizedByteValue> x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.BYTES;
    field_bytes = x;
  }
  public java.util.ArrayList<SizedByteValue> getBytes() {
    if (t == Tag.BYTES)
      return field_bytes;
    else
      return null;
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

  String field_float_ = null;
  public void setFloat(String x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.FLOAT_;
    field_float_ = x;
  }
  public String getFloat() {
    if (t == Tag.FLOAT_)
      return field_float_;
    else
      return null;
  }

  PointerValue field_pointer = null;
  public void setPointer(PointerValue x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.POINTER;
    field_pointer = x;
  }
  public PointerValue getPointer() {
    if (t == Tag.POINTER)
      return field_pointer;
    else
      return null;
  }

  public void setNullPointer() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.NULLPOINTER;
  }

  String field_filedescriptor = null;
  public void setFileDescriptor(String x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.FILEDESCRIPTOR;
    field_filedescriptor = x;
  }
  public String getFileDescriptor() {
    if (t == Tag.FILEDESCRIPTOR)
      return field_filedescriptor;
    else
      return null;
  }

  public void setFunction() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.FUNCTION;
  }

  java.util.ArrayList<LocalVarValue> field_array = null;
  public void setArray(java.util.ArrayList<LocalVarValue> x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.ARRAY;
    field_array = x;
  }
  public java.util.ArrayList<LocalVarValue> getArray() {
    if (t == Tag.ARRAY)
      return field_array;
    else
      return null;
  }

  FieldsInfo field_struct = null;
  public void setStruct(FieldsInfo x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.STRUCT;
    field_struct = x;
  }
  public FieldsInfo getStruct() {
    if (t == Tag.STRUCT)
      return field_struct;
    else
      return null;
  }

  FieldsInfo field_union = null;
  public void setUnion(FieldsInfo x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.UNION;
    field_union = x;
  }
  public FieldsInfo getUnion() {
    if (t == Tag.UNION)
      return field_union;
    else
      return null;
  }

  String field_enumconstant = null;
  public void setEnumConstant(String x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.ENUMCONSTANT;
    field_enumconstant = x;
  }
  public String getEnumConstant() {
    if (t == Tag.ENUMCONSTANT)
      return field_enumconstant;
    else
      return null;
  }

  LocalVarValueBitfield field_bitfield = null;
  public void setBitfield(LocalVarValueBitfield x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.BITFIELD;
    field_bitfield = x;
  }
  public LocalVarValueBitfield getBitfield() {
    if (t == Tag.BITFIELD)
      return field_bitfield;
    else
      return null;
  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    if (t == null)
      throw new JSONException("Uninitialized LocalVarValue");
    else {
      switch(t) {
      case BYTES:
         _out.append("[\"Bytes\",");
         _out.append("[");
         for (int i = 0; i < field_bytes.size(); ++i) {
           field_bytes.get(i).toJsonBuffer(_out);
           if (i < field_bytes.size() - 1)
             _out.append(",");
         }
         _out.append("]");
         _out.append("]");
         break;
      case INT_:
         _out.append("[\"Int\",");
         _out.append(String.valueOf(field_int_));
         _out.append("]");
         break;
      case FLOAT_:
         _out.append("[\"Float\",");
         Util.writeJsonString(_out, field_float_);
         _out.append("]");
         break;
      case POINTER:
         _out.append("[\"Pointer\",");
         field_pointer.toJsonBuffer(_out);
         _out.append("]");
         break;
      case NULLPOINTER:
        _out.append("\"NullPointer\"");
        break;
      case FILEDESCRIPTOR:
         _out.append("[\"FileDescriptor\",");
         Util.writeJsonString(_out, field_filedescriptor);
         _out.append("]");
         break;
      case FUNCTION:
        _out.append("\"Function\"");
        break;
      case ARRAY:
         _out.append("[\"Array\",");
         _out.append("[");
         for (int i = 0; i < field_array.size(); ++i) {
           field_array.get(i).toJsonBuffer(_out);
           if (i < field_array.size() - 1)
             _out.append(",");
         }
         _out.append("]");
         _out.append("]");
         break;
      case STRUCT:
         _out.append("[\"Struct\",");
         field_struct.toJsonBuffer(_out);
         _out.append("]");
         break;
      case UNION:
         _out.append("[\"Union\",");
         field_union.toJsonBuffer(_out);
         _out.append("]");
         break;
      case ENUMCONSTANT:
         _out.append("[\"EnumConstant\",");
         Util.writeJsonString(_out, field_enumconstant);
         _out.append("]");
         break;
      case BITFIELD:
         _out.append("[\"Bitfield\",");
         field_bitfield.toJsonBuffer(_out);
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
