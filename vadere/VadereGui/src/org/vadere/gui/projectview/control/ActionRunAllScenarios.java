package org.vadere.gui.projectview.control;

import javax.swing.*;

import org.vadere.gui.projectview.model.ProjectViewModel;

import java.awt.event.ActionEvent;

public class ActionRunAllScenarios extends AbstractAction {

	private final ProjectViewModel model;

	public ActionRunAllScenarios(final String name, final ProjectViewModel model) {
		super(name);
		this.model = model;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (model.runScenarioIsOk())
			model.getProject().runAllScenarios();
	}
}
