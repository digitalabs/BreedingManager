/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.browser.study.listeners;

import java.util.ArrayList;

import org.generationcp.browser.application.WelcomeTab;
import org.generationcp.browser.study.RepresentationDatasetComponent;
import org.generationcp.browser.study.StudyTreeComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.peholmst.i18n4vaadin.I18N;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Layout;

public class StudyButtonClickListener implements Button.ClickListener{

    private static final Logger LOG = LoggerFactory.getLogger(StudyButtonClickListener.class);
    private static final long serialVersionUID = 7921109465618354206L;

    private Layout source;
    
    private I18N i18n;

    public StudyButtonClickListener(Layout source, I18N i18n) {
        this.source = source;
        this.i18n = i18n;
    }

    @Override
    public void buttonClick(ClickEvent event) {
    	
        if (event.getComponent().getCaption().equals(i18n.getMessage("exportToCSV.label")) //"Export to CSV" 
        		&& (source instanceof RepresentationDatasetComponent)) {

            ((RepresentationDatasetComponent) source).exportToCSVAction();

        } else if ((event.getComponent().getCaption().equals(i18n.getMessage("study.button.label"))) //"I want to browse Studies and their Datasets"
                && (source instanceof WelcomeTab)) {
            ((WelcomeTab) source).browseStudiesAndDataSets();

        } else if (event.getComponent().getCaption().equals(i18n.getMessage("refresh.label")) //"Refresh") 
        		&& (source instanceof StudyTreeComponent)) {
            ((StudyTreeComponent) source).createTree();
        }

    }

}
