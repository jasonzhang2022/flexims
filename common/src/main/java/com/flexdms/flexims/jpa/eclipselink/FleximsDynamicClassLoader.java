package com.flexdms.flexims.jpa.eclipselink;

import java.lang.reflect.Field;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicClassWriter;
import org.eclipse.persistence.dynamic.EclipseLinkClassWriter;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;

/**
 * The purpose is to change the DynamicEntityImpl class
 * 
 * @author jason
 * 
 */
public class FleximsDynamicClassLoader extends DynamicClassLoader {

	static Field field;
	static {
		try {
			field = DynamicClassWriter.class.getDeclaredField("parentClass");
			field.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public FleximsDynamicClassLoader(ClassLoader delegate, DynamicClassWriter writer) {
		super(delegate, writer);
		correctDefaultWriter(writer);
	}

	public FleximsDynamicClassLoader(ClassLoader delegate) {
		super(delegate);
		correctDefaultWriter(getDefaultWriter());
	}

	/**
	 * Change the parent class of all DynamicEntity
	 * 
	 * @param writer
	 */
	public void correctDefaultWriter(DynamicClassWriter writer) {

		if (writer.getParentClass() == DynamicEntityImpl.class) {
			try {
				field.set(writer, FleximsDynamicEntityImpl.class);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void addClass(String className, EclipseLinkClassWriter writer) {
		if (writer instanceof DynamicClassWriter) {
			correctDefaultWriter((DynamicClassWriter) writer);
		}
		super.addClass(className, writer);
	}

}
