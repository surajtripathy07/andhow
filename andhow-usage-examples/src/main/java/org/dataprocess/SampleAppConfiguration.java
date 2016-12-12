package org.dataprocess;

import yarnandtail.andhow.property.StringProp;
import yarnandtail.andhow.property.IntProp;
import yarnandtail.andhow.*;
import yarnandtail.andhow.load.PropFileLoader;

/**
 * This is an example minimal application configuration.
 * IT DOES NOT WORK ON INITIAL STARTUP - READ ON...
 * 
 * This example will blowup when this class is first run.  In the console out,
 * there will be a complete example properties file.  Paste this output
 * (beginning and ending with ###### lines) into a properties file named
 * 'andhow.properties' in the project resources.
 * 
 * Fill in some sample properties run again - note the problem reporting in the
 * console when requirements and validations are violated.
 * 
 * As an example of a fully functioning properties file, there is an example one
 * named 'renamed_me_to_andhow.properties'
 * 
 * @author eeverman
 */
public class SampleAppConfiguration {
	
	public static void main(String[] args) {
		AndHow.builder()
				.addLoader(new PropFileLoader())
				.addGroup(AquariusConfig.class)
				.addGroup(NwisConfig.class)
				.build();
		
	
		//Fetching some properties from the AquariusConfig.
		//Note that the return values are strongly typed
		String queryUrl = AquariusConfig.SERVICE_URL.getValue() + AquariusConfig.QUERY_ENDPOINT.getValue();
		String itemUrl = AquariusConfig.SERVICE_URL.getValue() + AquariusConfig.ITEM_ENDPOINT.getValue();
		Integer timeout = AquariusConfig.TIMEOUT.getValue();
		
		System.out.println("The query url is: " + queryUrl);
		System.out.println("The query url is: " + itemUrl);
		System.out.println("Timeout is : " + timeout);
		
	}
	
	
	//
	// Here are two configuration groups that are added to the AppConfig, above.
	// Normally these would be in separate files, or even better, in files
	// within the modules they configure.  

	
	@PropertyGroupDescription(
			groupName="Aquarius Service Configuration", 
			groupDescription="Configures all communication to the USGS Aquarius service")
	public interface AquariusConfig extends PropertyGroup {
		StringProp SERVICE_URL = StringProp.builder().mustEndWith("/").build();
		IntProp TIMEOUT = IntProp.builder().setDefault(50).build();
		StringProp QUERY_ENDPOINT = StringProp.builder().build();
		StringProp ITEM_ENDPOINT = StringProp.builder().required().build();
	}
	
	@PropertyGroupDescription(
			groupName="NWIS Service Configuration", 
			groupDescription="Configures all communication to the USGS NWIS service")
	public interface NwisConfig extends PropertyGroup {
		StringProp SERVICE_URL = StringProp.builder().mustEndWith("/").build();
		IntProp TIMEOUT = IntProp.builder().setDefault(20).build();
		StringProp QUERY_ENDPOINT = StringProp.builder().build();
		StringProp ITEM_ENDPOINT = StringProp.builder().setRequired(true).build();
	}
	
}
