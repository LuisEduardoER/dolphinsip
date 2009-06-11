package by.bsu.fami.etl.compiler.bean;

public class Filter extends Component{
	
	protected String condition;
	
	public Filter(){
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
