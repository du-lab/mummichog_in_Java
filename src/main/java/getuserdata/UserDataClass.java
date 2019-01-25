package getuserdata;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import resources.Constants;

public class UserDataClass {
	private final static Logger LOGGER = Logger.getLogger(UserDataClass.class.getName());

	public static Map<String, String> cli_options(Map<String, String> opts) {
		Map<String, String> result = new HashMap<String, String>();
		result.putAll(Constants.optionsMap);

		Map<String, Boolean> boolMap = new HashMap<String, Boolean>();
		boolMap.putAll(Constants.booleanMap);

		Map<String, String> modesMap = new HashMap<String, String>();
		modesMap.putAll(Constants.modesMap);

		for (String key : opts.keySet()) {
			if (key.equalsIgnoreCase("-c") || key.equalsIgnoreCase("--cutoff"))
				result.put("cutoff", opts.get(key));
			else if (key.equalsIgnoreCase("-n") || key.equalsIgnoreCase("--network"))
				result.put("network", opts.get(key));
			else if (key.equalsIgnoreCase("-z") || key.equalsIgnoreCase("--force_primary_ion"))
				result.put("force_primary_ion", opts.get(key));
			else if (key.equalsIgnoreCase("-d") || key.equalsIgnoreCase("--modeling"))
				result.put("modeling", opts.get(key));
			else if (key.equalsIgnoreCase("-m") || key.equalsIgnoreCase("--mode"))
				result.put("mode", opts.get(key));
			else if (key.equalsIgnoreCase("-u") || key.equalsIgnoreCase("--instrument"))
				result.put("instrument", opts.get(key));
			else if (key.equalsIgnoreCase("-k") || key.equalsIgnoreCase("--workdir"))
				result.put("workdir", opts.get(key));
			else if (key.equalsIgnoreCase("-i") || key.equalsIgnoreCase("--input"))
				result.put("input", opts.get(key));
			else if (key.equalsIgnoreCase("-r") || key.equalsIgnoreCase("--reference"))
				result.put("reference", opts.get(key));
			else if (key.equalsIgnoreCase("-f") || key.equalsIgnoreCase("--infile"))
				result.put("infile", opts.get(key));
			else if (key.equalsIgnoreCase("-o") || key.equalsIgnoreCase("--output")) {
				result.put("output", opts.get(key).replace(".csv", ""));
				result.put("outdir",
						result.get("outdir") + System.currentTimeMillis() + opts.get(key).replace(".csv", ""));
			} else if (key.equalsIgnoreCase("-f") || key.equalsIgnoreCase("--infile"))
				result.put("infile", opts.get(key));
			else {
				System.out.println("Unsupported Option");
				LOGGER.error("Invalid Option");
			}
		}
		if (!(opts.containsKey("-o") || opts.containsKey("--output"))) {
				File file = new File(result.get("input"));
				result.put("outdir", file.getParent());
		}
		return result;
	}

	public static Map<String, String> dispatcher(String args[]) {
		Map<String, String> opts = new HashMap<String, String>();

		for (int i = 0; i < args.length;) {
			opts.put(args[i], args[i + 1]);
			i += 2;
		}

		return cli_options(opts);

	}
}
