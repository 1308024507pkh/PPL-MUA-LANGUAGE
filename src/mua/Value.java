package mua;

import java.util.ArrayList;

abstract class Value{
    private boolean isWord = false;
    private boolean isList = false;
    private boolean isRet = false;
    public Value(){}
    public Value(boolean isWord, boolean isList){
        this.isWord = isWord;
        this.isList = isList;
    }

    public void setRetFlag(boolean flag){
        isRet = flag;
    }
    public boolean getRetFlag(){
        return isRet;
    }

    public boolean ifWord(){
        return isWord;
    }
    public boolean ifList(){
        return isList;
    }
    public boolean ifBooLean(){
        return false;
    }
    public boolean ifNum(){
        return false; 
    }
    public boolean ifFunc(){
        return false;
    }
    abstract public String toString();
    abstract public boolean ifName();
    abstract public Value first();
    abstract public Value last();
    abstract public Value butfirst();
    abstract public Value butlast();
}

class ErrorValue extends Value{
    public ErrorValue(){
        super(false, false);
    }
    public String toString(){
        return new String("Error Value!");
    }
    public boolean ifName(){
        return false;
    }
    public Value first(){
        return new ErrorValue();
    }
    public Value last(){
        return new ErrorValue();
    }
    public Value butfirst(){
        return new ErrorValue();
    }
    public Value butlast(){
        return new ErrorValue();
    }
}

class Word extends Value{
    private String value;
    private boolean isBool = false;
    private boolean isNumber = false;

    public Word(String value){
        super(true, false);
        this.value = value;
    }

    public void setType(boolean isBool, boolean isNumber){
        this.isBool = isBool;
        this.isNumber = isNumber;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String toString(){
        return value;
    }

    public boolean ifName(){
        int i = 2;
        if(!ifOK1(value.charAt(0))) 
            return false;  
        for(;i < value.length(); i++)
            if(!ifOK2(value.charAt(i)))
                return false;
        return true;
    }
    
    public boolean ifBooLean(){
        return isBool;
    }
    
    public boolean ifNum(){
        return isNumber;
    }

    public Value first(){
        if(value.equals(""))    return new Word("");
        else{
            String subVal = value.substring(0, 1);
            return new Word(subVal);
        }
    }

    public Value last(){
        if(value.equals(""))    return new Word("");
        else{
            String subVal = value.substring(value.length()-1, value.length());
            return new Word(subVal);
        }
    }

    public Value butfirst(){
        if(value.equals("") || value.length()==1)   return new Word("");
        else{
            String subVal = value.substring(1, value.length());
            return new Word(subVal);
        }
    }

    public Value butlast(){
        if(value.equals("") || value.length()==1)   return new Word("");
        else{
            String subVal = value.substring(0, value.length()-1);
            return new Word(subVal);
        }
    }

    private boolean ifOK1(char c){
        if(c >= 'a' && c <= 'z')
            return true;
        if(c >= 'A' && c <= 'Z')
            return true;
        return false;
    }

    private boolean ifOK2(char c){
        if(c >= 'a' && c <= 'z')    
            return true;
        if(c >= 'A' && c <='Z')
            return true;
        if(c >= '0' && c <= '9')
            return true;
        if(c == '_')
            return true;
        return false;
    }
}

class MList extends Value{
    public ArrayList<Value> value;
    
    public MList(){
        super(false, true);
        value = new ArrayList<Value>();
    }

    public void addValue(Value val){
        value.add(val);
    }

    public String toString(){
        if(value.size()==0) return new String("[]");
        StringBuilder sb = new StringBuilder("[");
        sb.append(value.get(0).toString());
        for(int i=1; i<value.size(); i++)
            sb.append(" ").append(value.get(i).toString());
        sb.append("]");
        return sb.toString(); 
    }

    public boolean ifFunc(){
        if(value.size()==2 && value.get(0).ifList() && value.get(1).ifList())
            return true;
        else
            return false;
    }

    public boolean ifName(){
        return false;
    }

    public Value first(){
        if(value.size()==0) return new ErrorValue();
        else    return value.get(0);
    }

    public Value last(){
        if(value.size()==0) return new ErrorValue();
        else    return value.get(value.size()-1);
    }

    public Value butfirst(){
        MList l = new MList();
        if(value.size()==0 || value.size()==1)  return l;
        for(int i=1; i<value.size(); i++)   l.addValue(value.get(i));
        return l;
    }

    public Value butlast(){
        MList l = new MList();
        if(value.size()==0 || value.size()==1)  return l;
        for(int i=0; i<value.size()-1; i++)   l.addValue(value.get(i));
        return l;
    }
}

class Number extends Word{
    private double val;

    public Number(double val){
        super(String.valueOf(val));
        setType(false, true);
        this.val = val;
    }

    public Number(String val){
        super(val);
        setType(false, true);
        this.val = Double.parseDouble(val);
    }

    public Number(Word w){
        super(w.toString());
        setType(false, true);
        this.val = Double.parseDouble(w.toString());
    }

    public double getVal(){
        return val;
    }

    public Number add(Number other){
        return new Number(this.val+other.val);
    }

    public Number sub(Number other){
        return new Number(this.val-other.val);
    }

    public Number mul(Number other){
        return new Number(this.val*other.val);
    }

    public Number div(Number other){
        return new Number(this.val/other.val);
    }

    public Number mod(Number other){
        return new Number((double)((int)this.val%(int)other.val));
    }

    public String toString(){
        return String.valueOf(val); 
    }
}

class BooLean extends Word{
    private boolean val;

    public BooLean(boolean val){
        super(String.valueOf(val));
        setType(true, false);
        this.val = val;
    }

    public BooLean(String val){
        super(val);
        setType(true, false);
        if(val.equals("true"))
            this.val = true;
        else
            this.val = false;    
    } 

    public BooLean(Word w){
        super(w.toString());
        setType(true, false);
        if(w.toString().equals("true"))
            this.val = true;
        else
            this.val = false;
    }

    public boolean getVal(){
        return val;
    }
}
