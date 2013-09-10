package org.generationcp.browser.cross.study.h2h.main.pojos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FilterByLocation {

	String countryName;
	String trialEnvId;
	private List<String> provinceNameList  = new ArrayList();
	private List<String> locationStudyNameList  = new ArrayList();
	private Map<String, Map<String, LocationStudyDto>> provinceLocationStudyMap = new HashMap();
	
	
	//private Map<String, LocationStudyDto> locationStudyUniqueMap = new HashMap();
	
	public FilterByLocation(String country, String trialEnvId){
		this.countryName = country;
		this.trialEnvId = trialEnvId;
	}
	
	
	
	public String getTrialEnvId() {
		return trialEnvId;
	}



	public void setTrialEnvId(String trialEnvId) {
		this.trialEnvId = trialEnvId;
	}



	public String getCountryName() {
		return countryName;
	}


	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}	


	public List<String> getProvinceNameList() {
		return provinceNameList;
	}
	public void setProvinceNameList(List<String> provinceNameList) {
		this.provinceNameList = provinceNameList;
	}
	public List<String> getLocationStudyNameList() {
		return locationStudyNameList;
	}
	public void setLocationStudyNameList(List<String> locationStudyNameList) {
		this.locationStudyNameList = locationStudyNameList;
	}
	
	public void addProvinceAndLocationAndStudy(String provinceName, String locationName, String studyName){
		provinceNameList.add(provinceName);
		Map<String, LocationStudyDto> locationStudyUniqueMap = provinceLocationStudyMap.get(provinceName);
		if(locationStudyUniqueMap == null){
			locationStudyUniqueMap = new HashMap();
		}
		String key = locationName + ":" + studyName;
		
		LocationStudyDto dto = locationStudyUniqueMap.get(key);
		if(dto == null){
			dto = new LocationStudyDto(locationName, studyName);
			locationStudyUniqueMap.put(key, dto);
		}
		provinceLocationStudyMap.put(provinceName, locationStudyUniqueMap);
		//locationList.add(dto);
	}
	
	public Integer getNumberOfEnvironmentForCountry(){
		return provinceNameList.size();
	}
	
	public Set<String> getListOfProvinceNames(){
		return provinceLocationStudyMap.keySet();
	}
	public Integer getNumberOfEnvironmentForProvince(String provinceName){
		Map<String, LocationStudyDto> locationStudyUniqueMap = provinceLocationStudyMap.get(provinceName);
		return locationStudyUniqueMap.keySet().size();
	}
	public Collection<LocationStudyDto> getLocationStudyForProvince(String provinceName){
		Map<String, LocationStudyDto> locationStudyUniqueMap = provinceLocationStudyMap.get(provinceName);
		return locationStudyUniqueMap.values();
	}
	
	
		

}