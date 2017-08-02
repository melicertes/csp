package com.intrasoft.csp.ccs.commons.model;

import java.util.HashMap;
import java.util.Map;


/**
 * The specific object holds all the information related to Version.
 */
public class VersionDTO {

    /**
     * Minimum version of API supported, initial should be "1.0".
     */
    private double minVersion;

    /**
     * Maximum version of API.
     */
    private double maxVersion;

    private Map<String, String> versionContexts;
    
    /**
     * Default Constructor.
     */
    public VersionDTO() {
    }
    
    public VersionDTO withMinVersion(double min) {
    	minVersion = min;
    	return this;
    }
    
    public VersionDTO withMaxVersion(double max) {
    	maxVersion = max;
    	return this;
    }

    public VersionDTO withVersionContext(double version, String contextPrefix) {
    	if (null == versionContexts) {
    		versionContexts = new HashMap<String,String>();
    	}
    	versionContexts.put(Double.toString(version), contextPrefix);
    	return this;
    }
    
    /**
     * Returns the minimum supported Version.
     *
     * @return A double with the Version
     */
    public double getMinVersion() {
        return minVersion;
    }

    /**
     * Set the minimum supported Version.
     *
     * @param minVersion a double with the Version
     */
    public void setMinVersion(final double minVersion) {
        this.minVersion = minVersion;
    }

    /**
     * Returns the maximum supported Version.
     *
     * @return a double with maximum supported Version
     */
    public double getMaxVersion() {
        return maxVersion;
    }

    /**
     * Set the maximum supported Version.
     *
     * @param maxVersion a double with the maximum supported Version
     */
    public void setMaxVersion(final double maxVersion) {
        this.maxVersion = maxVersion;
    }


	public Map<String, String> getVersionContexts() {
		return versionContexts;
	}

	public void setVersionContexts(Map<String, String> versionContexts) {
		this.versionContexts = versionContexts;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VersionDTO [minVersion=").append(minVersion)
				.append(", maxVersion=").append(maxVersion)
				.append(", versionContexts=").append(versionContexts)
				.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(maxVersion);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minVersion);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((versionContexts == null) ? 0 : versionContexts.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VersionDTO))
			return false;
		VersionDTO other = (VersionDTO) obj;
		if (Double.doubleToLongBits(maxVersion) != Double
				.doubleToLongBits(other.maxVersion))
			return false;
		if (Double.doubleToLongBits(minVersion) != Double
				.doubleToLongBits(other.minVersion))
			return false;
		if (versionContexts == null) {
			if (other.versionContexts != null)
				return false;
		} else if (!versionContexts.equals(other.versionContexts))
			return false;
		return true;
	}
	
	
	
}
