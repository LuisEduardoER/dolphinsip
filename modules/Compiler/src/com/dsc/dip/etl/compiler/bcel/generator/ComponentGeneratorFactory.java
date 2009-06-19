package com.dsc.dip.etl.compiler.bcel.generator;

import com.dsc.dip.etl.compiler.bean.Checker;
import com.dsc.dip.etl.compiler.bean.Component;
import com.dsc.dip.etl.compiler.bean.DataReader;
import com.dsc.dip.etl.compiler.bean.DataWriter;

/**
 * Factory class, create specific instance of component generator by component
 * type.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public final class ComponentGeneratorFactory {

    /**
     * Disabled constructor. Factory class is util class.
     */
    private ComponentGeneratorFactory() {
    }

    /**
     * Create specific instance of component generator by component.
     * @param component
     *            - bean which contains all information about parsed component
     * @param componentType
     *            - real java class name for generated component
     * @return specific instance of component generator
     * @throws ClassNotFoundException
     *             didn't find java component class
     */
    public static ComponentGenerator instanceComponentGenerator(
            final Component component, final String componentType)
            throws ClassNotFoundException {
        if (component instanceof DataReader) {
            return new DataReaderGenerator((DataReader) component,
                    componentType);
        } else {
            if (component instanceof DataWriter) {
                return new DataWriterGenerator((DataWriter) component,
                        componentType);
            } else {
                if (component instanceof Checker) {
                    return new CheckerGenerator((Checker) component,
                            componentType);
                } else {
                    return new ComponentGenerator(component, componentType);
                }
            }
        }
    }
}
