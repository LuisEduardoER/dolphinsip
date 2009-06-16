package com.dsc.dip.etl.compiler.bcel.generator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.Type;

import com.dsc.dip.etl.compiler.bcel.RuleGenerator;
import com.dsc.dip.etl.compiler.bean.Component;


public abstract class ComponentWithDSGenerator extends ComponentGenerator {

	public ComponentWithDSGenerator(Component component, String componentType) {
		super(component, componentType);
	}

	protected void generateSetDataSource(InstructionFactory iFactory,
			InstructionList il, ConstantPoolGen constantPool, int index,
			String dataSource) throws ClassNotFoundException {
		il.append(InstructionFactory.createLoad(Type.getType(Class
				.forName(componentType)), index));
		il.append(InstructionFactory.createLoad(Type.getType(Class
				.forName(RuleGenerator.BASE_RULE_CLASS)), 0));
		il.append(new LDC(constantPool.addString((dataSource))));
		il.append(iFactory.createInvoke(RuleGenerator.BASE_RULE_CLASS,
				"findDataSource", Type.getType(Class
						.forName(RuleGenerator.BASE_DATASOURCE_CLASS)),
				new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));
		il.append(iFactory.createInvoke(componentType, "setDataSource",
				Type.VOID, new Type[] { Type.getType(Class
						.forName(RuleGenerator.BASE_DATASOURCE_CLASS)) },
				Constants.INVOKEVIRTUAL));
	}

}
