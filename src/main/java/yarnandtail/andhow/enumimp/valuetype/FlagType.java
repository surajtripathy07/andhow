package yarnandtail.andhow.enumimp.valuetype;

import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.enumimp.ConfigParamUtil;
import yarnandtail.andhow.enumimp.ConfigValueCollection;

/**
 *
 * @author eeverman
 */
public class FlagType extends BaseValueType<Boolean> {

	private static final FlagType instance = new FlagType();
	
	private FlagType() {
		super(Boolean.class, false, true, TrimStyle.TO_EMPTY);
	}
	
	public static FlagType get() {
		return instance;
	}
	
	public static FlagType instance() {
		return instance;
	}

	@Override
	public Boolean convert(String sourceValue) throws IllegalArgumentException {
		if (sourceValue != null) {

				
			String str = StringUtils.trimToEmpty(sourceValue.toString());
			if (str.isEmpty()) {
				return true;	//a flag is considered try just by its presence
			} else {
				return ConfigParamUtil.toBoolean(str);
			}


		} else {
			return false;
		}
	}
	
}
