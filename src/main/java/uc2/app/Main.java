package uc2.app;

import uc2.ui.BusinessRuleAppFrame;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        BusinessRuleAppFrame.open(ApplicationFactory.createController());
    }
}
