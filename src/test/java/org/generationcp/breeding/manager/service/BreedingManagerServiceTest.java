
package org.generationcp.breeding.manager.service;

import java.util.List;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 9/26/2014 Time: 2:09 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class BreedingManagerServiceTest {

	private static final Integer DUMMY_USER_ID = 1;
	private static final Integer DUMMY_PERSON_ID = 1;

	private static final String SAMPLE_SEARCH_STRING = "a sample search string";
	private static final Operation CONTAINS_MATCH = Operation.LIKE;
	private static final Boolean INCLUDE_PARENT = true;
	private static final Boolean WITH_INVENTORY_ONLY = true;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private GermplasmListManager germplasmListManager;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private UserDataManager userDataManager;

	@InjectMocks
	private BreedingManagerServiceImpl breedingManagerService;

	@Before
	public void setUp() {
	}

	@Test
	public void testGetOwnerListNamePositiveScenario() {

		User sampleUser = Mockito.mock(User.class);
		Person p = this.createDummyPerson();

		try {

			// the following is code used to set up the positive scenario
			Mockito.when(this.userDataManager.getUserById(BreedingManagerServiceTest.DUMMY_USER_ID)).thenReturn(sampleUser);
			Mockito.when(sampleUser.getPersonid()).thenReturn(BreedingManagerServiceTest.DUMMY_PERSON_ID);

			// we set up the test so that the dummy person object we created will be the one used by the service
			Mockito.when(this.userDataManager.getPersonById(BreedingManagerServiceTest.DUMMY_PERSON_ID)).thenReturn(p);

			// actual verification portion
			String name = this.breedingManagerService.getOwnerListName(BreedingManagerServiceTest.DUMMY_USER_ID);

			Assert.assertEquals("Generated owner name is not correct", p.getFirstName() + " " + p.getMiddleName() + " " + p.getLastName(),
					name);

		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetOwnerListNameNoPerson() {
		User sampleUser = Mockito.mock(User.class);
		final String dummyUserName = "USER NAME";
		try {

			// the following is code used to set up the positive scenario
			Mockito.when(this.userDataManager.getUserById(BreedingManagerServiceTest.DUMMY_USER_ID)).thenReturn(sampleUser);
			Mockito.when(sampleUser.getPersonid()).thenReturn(BreedingManagerServiceTest.DUMMY_PERSON_ID);

			// we set up the test so that the dummy person object we created will be the one used by the service
			Mockito.when(this.userDataManager.getPersonById(BreedingManagerServiceTest.DUMMY_PERSON_ID)).thenReturn(null);
			Mockito.when(sampleUser.getName()).thenReturn(dummyUserName);

			// actual verification portion
			String name = this.breedingManagerService.getOwnerListName(BreedingManagerServiceTest.DUMMY_USER_ID);

			Assert.assertEquals("Generated owner name is not correct", dummyUserName, name);

		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetDefaultOwnerList() {
		User sampleUser = Mockito.mock(User.class);
		Person p = this.createDummyPerson();

		try {

			// the following is code used to set up the positive scenario
			this.setUpGetCurrentUserLocalId();
			Mockito.when(this.userDataManager.getUserById(BreedingManagerServiceTest.DUMMY_USER_ID)).thenReturn(sampleUser);
			Mockito.when(sampleUser.getPersonid()).thenReturn(BreedingManagerServiceTest.DUMMY_PERSON_ID);

			// we set up the test so that the dummy person object we created will be the one used by the service
			Mockito.when(this.userDataManager.getPersonById(BreedingManagerServiceTest.DUMMY_PERSON_ID)).thenReturn(p);

			// actual verification portion
			String name = this.breedingManagerService.getOwnerListName(BreedingManagerServiceTest.DUMMY_USER_ID);

			Assert.assertEquals("Generated owner name is not correct", p.getFirstName() + " " + p.getMiddleName() + " " + p.getLastName(),
					name);

		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
	}

	protected void setUpGetCurrentUserLocalId() throws MiddlewareQueryException {
		final Long dummyProjectId = (long) 1;
		Project project = Mockito.mock(Project.class);
		WorkbenchRuntimeData runtimeData = Mockito.mock(WorkbenchRuntimeData.class);

		Mockito.when(this.workbenchDataManager.getWorkbenchRuntimeData()).thenReturn(runtimeData);
		Mockito.when(runtimeData.getUserId()).thenReturn(BreedingManagerServiceTest.DUMMY_USER_ID);

		Mockito.when(this.workbenchDataManager.getLastOpenedProject(BreedingManagerServiceTest.DUMMY_USER_ID)).thenReturn(project);
		Mockito.when(project.getProjectId()).thenReturn(dummyProjectId);

		Mockito.when(this.workbenchDataManager.getLocalIbdbUserId(BreedingManagerServiceTest.DUMMY_USER_ID, dummyProjectId)).thenReturn(
				BreedingManagerServiceTest.DUMMY_USER_ID);
	}

	@Test
	public void testDoGermplasmSearch() throws Exception {
		List<Germplasm> expectedResult = Mockito.mock(List.class);
		expectedResult.add(Mockito.mock(Germplasm.class));

		Mockito.when(
				this.germplasmDataManager.searchForGermplasm(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
						BreedingManagerServiceTest.CONTAINS_MATCH, BreedingManagerServiceTest.INCLUDE_PARENT,
						BreedingManagerServiceTest.WITH_INVENTORY_ONLY)).thenReturn(expectedResult);

		// assume we have a search result
		List<Germplasm> result =
				this.breedingManagerService.doGermplasmSearch(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
						BreedingManagerServiceTest.CONTAINS_MATCH, BreedingManagerServiceTest.INCLUDE_PARENT,
						BreedingManagerServiceTest.WITH_INVENTORY_ONLY);

		Mockito.verify(this.germplasmDataManager).searchForGermplasm(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
				BreedingManagerServiceTest.CONTAINS_MATCH, BreedingManagerServiceTest.INCLUDE_PARENT,
				BreedingManagerServiceTest.WITH_INVENTORY_ONLY);

		Assert.assertTrue("expects the result size is equal to the expectedResult size", result.size() == expectedResult.size());

	}

	@Test
	public void testDoGermplasmSearchEmptyString() throws Exception {

		try {
			this.breedingManagerService.doGermplasmSearch("", BreedingManagerServiceTest.CONTAINS_MATCH,
					BreedingManagerServiceTest.INCLUDE_PARENT, BreedingManagerServiceTest.WITH_INVENTORY_ONLY);
			Assert.fail("expects an error since germplasm search string is empty");
		} catch (BreedingManagerSearchException e) {
			Assert.assertEquals("Should throw a BreedingManagerSearchException with SEARCH_QUERY_CANNOT_BE_EMPTY message",
					e.getErrorMessage(), Message.SEARCH_QUERY_CANNOT_BE_EMPTY);
			Mockito.verifyZeroInteractions(this.germplasmDataManager); // germplasmListManager should not be called
		}
	}

	@Test
	public void testDoGermplasmListSearch() throws Exception {
		List<GermplasmList> expectedResult = Mockito.mock(List.class);
		expectedResult.add(Mockito.mock(GermplasmList.class));

		Mockito.when(
				this.germplasmListManager.searchForGermplasmList(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
						BreedingManagerServiceTest.CONTAINS_MATCH)).thenReturn(expectedResult);

		// assume we have a search result
		List<GermplasmList> result =
				this.breedingManagerService.doGermplasmListSearch(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
						BreedingManagerServiceTest.CONTAINS_MATCH);

		Mockito.verify(this.germplasmListManager).searchForGermplasmList(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
				BreedingManagerServiceTest.CONTAINS_MATCH);

		Assert.assertTrue("expects the result size is equal to the expectedResult size", result.size() == expectedResult.size());
	}

	@Test
	public void testDoGermplasmListSearchEmptyString() throws Exception {

		try {
			this.breedingManagerService.doGermplasmListSearch("", BreedingManagerServiceTest.CONTAINS_MATCH);
			Assert.fail("expects an error since germplasm search string is empty");
		} catch (BreedingManagerSearchException e) {
			Assert.assertEquals("Should throw a BreedingManagerSearchException with SEARCH_QUERY_CANNOT_BE_EMPTY message",
					e.getErrorMessage(), Message.SEARCH_QUERY_CANNOT_BE_EMPTY);
			Mockito.verifyZeroInteractions(this.germplasmListManager); // germplasmListManager should not be called
		}
	}

	@Test
	public void testValidateEmptySearchString() throws Exception {
		try {
			this.breedingManagerService.validateEmptySearchString("");
			Assert.fail("expects a BreedingManagerSearchException to be thrown");
		} catch (BreedingManagerSearchException e) {
			Assert.assertEquals("Should throw a BreedingManagerSearchException with SEARCH_QUERY_CANNOT_BE_EMPTY message",
					e.getErrorMessage(), Message.SEARCH_QUERY_CANNOT_BE_EMPTY);
		}
	}

	protected Person createDummyPerson() {
		final String firstName = "FIRST NAME";
		final String middleName = "MIDDLE NAME";
		final String lastName = "LAST NAME";

		return new Person(firstName, middleName, lastName);
	}

}
