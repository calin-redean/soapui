package com.eviware.soapui.security.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.security.SecurityCheckedParameter;
import com.eviware.soapui.model.support.ModelSupport;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.security.SecurityTest;
import com.eviware.soapui.security.check.AbstractSecurityCheckWithProperties;
import com.eviware.soapui.support.StringUtils;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;
import com.eviware.soapui.support.components.ModelItemListDesktopPanel;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormField;
import com.eviware.x.form.XFormFieldListener;
import com.eviware.x.form.XFormOptionsField;
import com.eviware.x.form.support.ADialogBuilder;
import com.eviware.x.form.support.AField;
import com.eviware.x.form.support.AForm;
import com.eviware.x.form.support.XFormMultiSelectList;
import com.eviware.x.form.support.AField.AFieldType;

public class CloneSecurityCheckParameterAction extends AbstractSoapUIAction<AbstractSecurityCheckWithProperties>
{
	public static final String SOAPUI_ACTION_ID = "CloneSecurityCheckParameterAction";

	private TestCase testCase;
	private Project project;
	private XFormDialog dialog;
	private AbstractSecurityCheckWithProperties securityCheck;

	public CloneSecurityCheckParameterAction()
	{
		super( "Clone SecurityCheck Parameters", "Clones an arbitrary number of security check paramaters" );
	}

	public void perform( final AbstractSecurityCheckWithProperties securityCheck, Object param )
	{
		this.securityCheck = securityCheck;
		testCase = securityCheck.getTestStep().getTestCase();
		project = testCase.getTestSuite().getProject();

		if( dialog == null )
		{
			dialog = ADialogBuilder.buildDialog( CloneParameterDialog.class );
			dialog.getFormField( CloneParameterDialog.TARGET_TESTSUITE ).addFormFieldListener( new XFormFieldListener()
			{
				public void valueChanged( XFormField sourceField, String newValue, String oldValue )
				{
					TestSuite testSuite = project.getTestSuiteByName( newValue );
					String[] testCaseNames = ModelSupport.getNames( testSuite.getTestCaseList() );
					dialog.setOptions( CloneParameterDialog.TARGET_TESTCASE, testCaseNames );

					if( testCaseNames.length > 0 )
					{
						dialog.setValue( CloneParameterDialog.TARGET_TESTCASE, testCaseNames[0] );
						TestCase testCase = testSuite.getTestCaseByName( testCaseNames[0] );

						String[] testStepNames = ModelSupport.getNames( testCase.getTestStepList() );
						dialog.setOptions( CloneParameterDialog.TARGET_TESTSTEP, testStepNames );

						String[] securityTestNames = ModelSupport.getNames( testCase.getSecurityTestList() );
						dialog.setOptions( CloneParameterDialog.TARGET_SECURITYTEST, securityTestNames );

						if( testStepNames.length > 0 )
						{
							dialog.setValue( CloneParameterDialog.TARGET_TESTSTEP, testStepNames[0] );
						}
						else
						{
							dialog.setOptions( CloneParameterDialog.TARGET_TESTSTEP, new String[0] );
						}

						if( securityTestNames.length > 0 )
						{
							dialog.setValue( CloneParameterDialog.TARGET_SECURITYTEST, securityTestNames[0] );
						}
						else
						{
							dialog.setOptions( CloneParameterDialog.TARGET_SECURITYTEST, new String[0] );
						}
					}
					else
					{
						dialog.setOptions( CloneParameterDialog.TARGET_SECURITYTEST, new String[0] );
						dialog.setOptions( CloneParameterDialog.TARGET_TESTSTEP, new String[0] );
					}
				}
			} );
			dialog.getFormField( CloneParameterDialog.TARGET_TESTCASE ).addFormFieldListener( new XFormFieldListener()
			{
				public void valueChanged( XFormField sourceField, String newValue, String oldValue )
				{
					String testSuiteName = dialog.getValue( CloneParameterDialog.TARGET_TESTSUITE );
					TestSuite testSuite = project.getTestSuiteByName( testSuiteName );
					TestCase testCase = testSuite.getTestCaseByName( newValue );

					String[] testStepNames = ModelSupport.getNames( testCase.getTestStepList() );
					dialog.setOptions( CloneParameterDialog.TARGET_TESTSTEP, testStepNames );
					String[] securityTestNames = ModelSupport.getNames( testCase.getSecurityTestList() );
					dialog.setOptions( CloneParameterDialog.TARGET_SECURITYTEST, securityTestNames );

					if( testStepNames.length > 0 )
					{
						dialog.setValue( CloneParameterDialog.TARGET_TESTSTEP, testStepNames[0] );
					}
					else
					{
						dialog.setOptions( CloneParameterDialog.TARGET_TESTSTEP, new String[0] );
					}

					if( securityTestNames.length > 0 )
					{
						dialog.setValue( CloneParameterDialog.TARGET_SECURITYTEST, securityTestNames[0] );
					}
					else
					{
						dialog.setOptions( CloneParameterDialog.TARGET_SECURITYTEST, new String[0] );
					}
				}
			} );
			dialog.getFormField( CloneParameterDialog.TARGET_TESTSTEP ).addFormFieldListener( new XFormFieldListener()
			{
				public void valueChanged( XFormField sourceField, String newValue, String oldValue )
				{
					String testSuiteName = dialog.getValue( CloneParameterDialog.TARGET_TESTSUITE );
					TestSuite testSuite = project.getTestSuiteByName( testSuiteName );
					String testCaseName = dialog.getValue( CloneParameterDialog.TARGET_TESTCASE );
					TestCase testCase = testSuite.getTestCaseByName( testCaseName );
					String securityTestName = dialog.getValue( CloneParameterDialog.TARGET_SECURITYTEST );
					SecurityTest securityTest = testCase.getSecurityTestByName( securityTestName );
					TestStep testStep = testCase.getTestStepByName( newValue );

					String[] securityCheckNames = ModelSupport.getNames( securityTest.getTestStepSecurityChecks( testStep
							.getId() ) );
					dialog.setOptions( CloneParameterDialog.TARGET_SECURITYCHECK, securityCheckNames );
				}
			} );
			dialog.getFormField( CloneParameterDialog.TARGET_SECURITYTEST ).addFormFieldListener( new XFormFieldListener()
			{
				public void valueChanged( XFormField sourceField, String newValue, String oldValue )
				{
					String testSuiteName = dialog.getValue( CloneParameterDialog.TARGET_TESTSUITE );
					TestSuite testSuite = project.getTestSuiteByName( testSuiteName );
					String testCaseName = dialog.getValue( CloneParameterDialog.TARGET_TESTCASE );
					TestCase testCase = testSuite.getTestCaseByName( testCaseName );
					SecurityTest securityTest = testCase.getSecurityTestByName( newValue );
					String testStepName = dialog.getValue( CloneParameterDialog.TARGET_TESTSTEP );
					TestStep testStep = testCase.getTestStepByName( testStepName );

					String[] securityCheckNames = ModelSupport.getNames( securityTest.getTestStepSecurityChecks( testStep
							.getId() ) );
					dialog.setOptions( CloneParameterDialog.TARGET_SECURITYCHECK, securityCheckNames );
				}
			} );
			dialog.addAction( new ApplyAction() );
		}
		WsdlTestCase testCase = ( WsdlTestCase )securityCheck.getTestStep().getTestCase();

		dialog.setOptions( CloneParameterDialog.TARGET_TESTSUITE,
				ModelSupport.getNames( testCase.getTestSuite().getProject().getTestSuiteList() ) );
		dialog.setValue( CloneParameterDialog.TARGET_TESTSUITE, testCase.getTestSuite().getName() );

		List<TestCase> testCaseList = testCase.getTestSuite().getTestCaseList();
		dialog.setOptions( CloneParameterDialog.TARGET_TESTCASE, ModelSupport.getNames( testCaseList ) );
		dialog.setValue( CloneParameterDialog.TARGET_TESTCASE, testCase.getName() );

		dialog.setOptions( CloneParameterDialog.TARGET_TESTSTEP, ModelSupport.getNames( testCase.getTestStepList() ) );
		dialog.setOptions( CloneParameterDialog.TARGET_SECURITYTEST,
				ModelSupport.getNames( testCase.getSecurityTestList() ) );

		String securityTestName = dialog.getValue( CloneParameterDialog.TARGET_SECURITYTEST );
		SecurityTest securityTest = testCase.getSecurityTestByName( securityTestName );
		String testStepName = dialog.getValue( CloneParameterDialog.TARGET_TESTSTEP );
		TestStep testStep = testCase.getTestStepByName( testStepName );

		String[] securityCheckNames = ModelSupport.getNames( securityTest.getTestStepSecurityChecks( testStep.getId() ) );
		dialog.setOptions( CloneParameterDialog.TARGET_SECURITYCHECK, securityCheckNames );

		dialog.setOptions( CloneParameterDialog.PARAMETERS, securityCheck.getParameterHolder().getParameterLabels() );

		if( dialog.show() )
		{
			List<ModelItem> items = performClone();

			if( items.size() > 0 )
			{
				UISupport.showDesktopPanel( new ModelItemListDesktopPanel( "Updated SecurityChecks",
						"The following SecurityChecks where updated with new parameters", items.toArray( new ModelItem[items
								.size()] ) ) );
			}
			else
			{
				UISupport.showInfoMessage( "Updated " + items.size() + " checks" );
			}
		}
	}

	public List<ModelItem> performClone()
	{
		List<ModelItem> items = new ArrayList<ModelItem>();
		String targetTestSuiteName = dialog.getValue( CloneParameterDialog.TARGET_TESTSUITE );
		String targetTestCaseName = dialog.getValue( CloneParameterDialog.TARGET_TESTCASE );
		String targetSecurityTestName = dialog.getValue( CloneParameterDialog.TARGET_SECURITYTEST );
		String targetSecurityTestStepName = dialog.getValue( CloneParameterDialog.TARGET_TESTSTEP );
		String[] securityChecks = StringUtils.toStringArray( ( ( XFormMultiSelectList )dialog
				.getFormField( CloneParameterDialog.TARGET_SECURITYCHECK ) ).getSelectedOptions() );

		if( securityChecks.length == 0 )
		{
			UISupport.showErrorMessage( "No SecurityChecks selected.." );
			return items;
		}

		int[] indexes = ( ( XFormOptionsField )dialog.getFormField( CloneParameterDialog.PARAMETERS ) )
				.getSelectedIndexes();
		if( indexes.length == 0 )
		{
			UISupport.showErrorMessage( "No Parameters selected.." );
			return items;
		}

		Project project = testCase.getTestSuite().getProject();
		TestSuite targetTestSuite = project.getTestSuiteByName( targetTestSuiteName );
		TestCase targetTestCase = targetTestSuite.getTestCaseByName( targetTestCaseName );
		SecurityTest targetSecurityTest = targetTestCase.getSecurityTestByName( targetSecurityTestName );
		TestStep targetTestStep = targetTestCase.getTestStepByName( targetSecurityTestStepName );

		boolean overwrite = dialog.getBooleanValue( CloneParameterDialog.OVERWRITE );

		for( String checkName : securityChecks )
		{
			AbstractSecurityCheckWithProperties targetSecurityCheck = ( AbstractSecurityCheckWithProperties )targetSecurityTest
					.getTestStepSecurityCheckByName( targetTestStep.getId(), checkName );
			// if( dialog.getBooleanValue( Form.CLEAR_EXISTING ) )
			// {
			// while( testRequest.getAssertionCount() > 0 )
			// {
			// testRequest.removeAssertion( testRequest.getAssertionAt( 0 ) );
			// }
			// }

			for( int i : indexes )
			{
				SecurityCheckedParameter checkParameter = securityCheck.getParameterAt( i );
				String newParameterName = checkParameter.getName();
				if( securityCheck.getParameterByName( checkParameter.getName() ) != null )
				{
					if( securityCheck.equals( targetSecurityCheck ) )
					{
						newParameterName = "Copy of " + checkParameter.getName();
					}
					else
					{
						newParameterName = newParameterName + "1";
					}
				}
				if( targetSecurityCheck.importParameter( checkParameter, overwrite, newParameterName )
						&& !items.contains( targetSecurityCheck ) )
				{
					items.add( targetSecurityCheck );
				}
			}
		}
		return items;
	}

	@AForm( description = "Specify target TestSuite/TestCase/Security Test(s)/Security Check(s) and select Parameters to clone", name = "Clone Parameters", icon = UISupport.TOOL_ICON_PATH )
	interface CloneParameterDialog
	{
		@AField( name = "Parameters", description = "The Parameters to clone", type = AFieldType.MULTILIST )
		public final static String PARAMETERS = "Parameters";

		@AField( name = "SecurityChecks", description = "The SecurityChecks to clone to", type = AFieldType.MULTILIST )
		public final static String TARGET_SECURITYCHECK = "SecurityChecks";

		@AField( name = "Target TestStep", description = "The target TestStep for the cloned Parameter(s)", type = AFieldType.ENUMERATION )
		public final static String TARGET_TESTSTEP = "Target TestStep";

		@AField( name = "Target SecurityTest", description = "The target SecurityTest for the cloned Parameter(s)", type = AFieldType.ENUMERATION )
		public final static String TARGET_SECURITYTEST = "Target SecurityTest";

		@AField( name = "Target TestCase", description = "The target TestCase for the cloned Parameter(s)", type = AFieldType.ENUMERATION )
		public final static String TARGET_TESTCASE = "Target TestCase";

		@AField( name = "Target TestSuite", description = "The target TestSuite for the cloned Parameter(s)", type = AFieldType.ENUMERATION )
		public final static String TARGET_TESTSUITE = "Target TestSuite";

		@AField( name = "Overwrite", description = "Overwrite existing parameters", type = AFieldType.BOOLEAN )
		public final static String OVERWRITE = "Overwrite";
	}

	private class ApplyAction extends AbstractAction
	{
		private ApplyAction()
		{
			super( "Apply" );

			putValue( SHORT_DESCRIPTION, "Applies current configuration" );
		}

		public void actionPerformed( ActionEvent e )
		{
			List<ModelItem> items = performClone();
			UISupport.showInfoMessage( "Updated  " + items.size() + " items" );
		}
	}

}
