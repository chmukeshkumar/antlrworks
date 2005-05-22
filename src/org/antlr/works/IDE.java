package org.antlr.works;

/*

[The "BSD licence"]
Copyright (c) 2004 Jean Bovet
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

import edu.usfca.xj.appkit.app.XJApplication;
import edu.usfca.xj.appkit.app.XJApplicationDelegate;
import edu.usfca.xj.appkit.document.XJDataPlainText;
import edu.usfca.xj.appkit.frame.XJPanel;
import edu.usfca.xj.appkit.swing.XJLookAndFeel;
import edu.usfca.xj.appkit.utils.XJLocalizable;
import org.antlr.works.dialog.DialogPersonalInfo;
import org.antlr.works.dialog.DialogPrefs;
import org.antlr.works.editor.EditorPreferences;
import org.antlr.works.editor.EditorWindow;
import org.antlr.works.util.Statistics;

import javax.swing.*;
import java.io.IOException;
import java.io.File;

public class IDE extends XJApplicationDelegate {

    public static SplashScreen sc;

    public static void main(String[] args) {

       /* System.out.println("A: "+System.getProperty("user.dir"));
        System.out.println("B: "+System.getProperty("user.home"));
                try
        {
            File currDir = new File(".");

            System.out.println ( "Dir : " + currDir );
            System.out.println ( "Dir : " + currDir.getCanonicalPath() );
        }
        catch (IOException ioEx )
        {
            System.out.println ("Error : " + ioEx );
        }
        System.exit(0);   */

        sc = new SplashScreen();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    sc.setVisible(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        XJApplication.run(new IDE());
    }

    public void appDidLaunch() {

        XJLookAndFeel.applyLookAndFeel(EditorPreferences.getLookAndFeel());

        XJApplication.setPropertiesPath("org/antlr/works/properties/");
        XJApplication.addDocumentType(Document.class, EditorWindow.class, XJDataPlainText.class, "g", XJLocalizable.getString("strings", "GrammarDocumentType"));

        registerUser();

        switch (EditorPreferences.getStartupAction()) {
            case EditorPreferences.STARTUP_NEW_DOC:
                XJApplication.shared().newDocument();
                break;
            case EditorPreferences.STARTUP_OPEN_LAST_DOC:
                if (XJApplication.shared().getDocuments().size() == 0) {
                    if (XJApplication.shared().openLastUsedDocument() == false)
                        XJApplication.shared().newDocument();
                }
                break;
        }
        sc.setVisible(false);
    }

    public void registerUser() {
        if(!EditorPreferences.isUserRegistered()) {
            sc.setVisible(false);
            new DialogPersonalInfo().runModal();
            EditorPreferences.setUserRegistered(true);
        }
    }

    public void appShowHelp() {
        Statistics.shared().recordEvent(Statistics.EVENT_SHOW_HELP);
    }

    public void appWillTerminate() {
        Statistics.shared().close();
    }

    public Class appPreferencesPanelClass() {
        return DialogPrefs.class;
    }

    public boolean appHasPreferencesMenuItem() {
        return true;
    }

    public boolean appShouldQuitAfterLastWindowClosed() {
        return false;
    }

    public Class appPreferencesClass() {
        return IDE.class;
    }

}