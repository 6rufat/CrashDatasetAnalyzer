package Objects;

public class FlaggedObject {
    Object object;
    Boolean flag;

    public FlaggedObject() {
    }

    public FlaggedObject(Object a, Boolean b) {
	this.object = a;
	this.flag = b;
    }

    public Object getObject() {
	return object;
    }

    public void setObject(Object a) {
	this.object = a;
    }

    public Boolean getFlag() {
	return flag;
    }

    public void setFlag(Boolean b) {
	this.flag = b;
    }

}
