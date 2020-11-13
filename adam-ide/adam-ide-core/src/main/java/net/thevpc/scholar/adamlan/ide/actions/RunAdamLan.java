/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.ide.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.thevpc.scholar.adamlan.AdamLanParserImpl;
import net.thevpc.scholar.adamlan.ide.editor.MainEditorTopComponent;
import net.thevpc.scholar.adamlan.ide.editor.NbAdamLanExecContext;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

@ActionID(
        category = "Edit",
        id = "net.thevpc.scholar.adam.adam.ide.RunAdamLan"
)
@ActionRegistration(
        iconBase = "net/thevpc/scholar/adamlan/ide/run-icon.png",
        displayName = "#CTL_RunAdamLan"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1200)
    ,
  @ActionReference(path = "Toolbars/File", position = 300)
})
@Messages("CTL_RunAdamLan=RunAdamLan")
public final class RunAdamLan implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        MainEditorTopComponent tc = (MainEditorTopComponent) WindowManager.getDefault().findTopComponent("MainEditorTopComponent");
        String text = tc.getCodeText();
        NbAdamLanExecContext env = new NbAdamLanExecContext();
        AdamLanParserImpl p = new AdamLanParserImpl(env);
        p.evaluate(text);
    }
}
