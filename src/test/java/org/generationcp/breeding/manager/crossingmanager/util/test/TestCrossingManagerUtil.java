package org.generationcp.breeding.manager.crossingmanager.util.test;

import junit.framework.Assert;

import org.generationcp.breeding.manager.crossingmanager.util.CrossingManagerUtil;
import org.generationcp.middleware.pojos.Germplasm;
import org.junit.Test;

public class TestCrossingManagerUtil {

	@Test
	public void testSetCrossingBreedingMethod() {
		//prepare test data
		Germplasm gc = new Germplasm();
		
		//test case 1 - expected result is gc method id = 101
		Germplasm male1 = new Germplasm();
		male1.setGnpgs(Integer.valueOf(-1));
		Germplasm female1 = new Germplasm();
		female1.setGnpgs(Integer.valueOf(-1));
		gc = CrossingManagerUtil.setCrossingBreedingMethod(gc, female1, male1, null, null, null, null);
		Assert.assertEquals(Integer.valueOf(101), gc.getMethodId());
		
		//test case 2 - expected result is gc method id = 101
		Germplasm male2 = new Germplasm();
		male2.setGnpgs(Integer.valueOf(1));
		gc = CrossingManagerUtil.setCrossingBreedingMethod(gc, female1, male2, null, null, null, null);
		Assert.assertEquals(Integer.valueOf(101), gc.getMethodId());
		
		//test case 3 - expected result is gc method id = 107
		Germplasm female3 = new Germplasm();
		female3.setGid(Integer.valueOf(1));
		female3.setGnpgs(Integer.valueOf(-1));
		Germplasm male3 = new Germplasm();
		male3.setGnpgs(Integer.valueOf(2));
		Germplasm mommyOfMale3 = new Germplasm();
		mommyOfMale3.setGid(Integer.valueOf(1));
		gc = CrossingManagerUtil.setCrossingBreedingMethod(gc, female3, male3, null, null, mommyOfMale3, null);
		Assert.assertEquals(Integer.valueOf(107), gc.getMethodId());
		
		//test case 4 - expected result is gc method id = 102
		Germplasm female4 = new Germplasm();
		female4.setGid(Integer.valueOf(1));
		female4.setGnpgs(Integer.valueOf(-1));
		Germplasm male4 = new Germplasm();
		male4.setGnpgs(Integer.valueOf(2));
		Germplasm mommyOfMale4 = new Germplasm();
		mommyOfMale4.setGid(Integer.valueOf(2));
		Germplasm daddyOfMale4 = new Germplasm();
		daddyOfMale4.setGid(Integer.valueOf(3));
		gc = CrossingManagerUtil.setCrossingBreedingMethod(gc, female4, male4, null, null, mommyOfMale4, daddyOfMale4);
		Assert.assertEquals(Integer.valueOf(102), gc.getMethodId());
		
		//test case 5 - expected result is gc method id = 106
		Germplasm male5 = new Germplasm();
		male5.setGnpgs(Integer.valueOf(5));
		gc = CrossingManagerUtil.setCrossingBreedingMethod(gc, female1, male5, null, null, null, null);
		Assert.assertEquals(Integer.valueOf(106), gc.getMethodId());
	}

}