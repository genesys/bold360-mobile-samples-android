// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

import UIKit
import BoldAIEngine
import BoldUI

class ViewController: UIViewController {
    
    var chatController: NRChatController!
    var chatControllerDelegate: NRChatControllerDelegate!
    var chatHandlerProvider: ChatHandlerProvider!
    var chatViewController: UIViewController!

    override func viewDidLoad() {
        super.viewDidLoad()
    }

    @IBAction func setupBoldChat(_ sender: Any) {
        // 1. create account & set
        let account = self.createAccount()
        self.chatController = NRChatController(account: account)
        // 2.  set controller delegate
        self.chatController.delegate = self
        // 3. create configuration & set
        let config = self.createConfiguration()
        self.chatController.uiConfiguration = config
        
        // 4. chat controller initialize
        self.chatController.initialize = { controller, configuration, error in
            if let vc = controller {
                self.chatViewController = vc
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
    
    func createAccount() -> AccountParams {
        let account = AccountParams()
        account.account = ""
        account.knowledgeBase = ""
        account.apiKey = ""
                
        return account;
    }
    
    func createConfiguration() -> NRBotConfiguration {
        let config: NRBotConfiguration = NRBotConfiguration()
        config.chatContentURL = URL(string:"https://cdn-customers.nanorep.com/v3/view-default.html")
        config.withNavBar = true
        
        return config
    }
}

extension ViewController: NRChatControllerDelegate {
    func statement(_ statement: StorableChatElement!, didFailWithError error: Error!) {
        print(error.localizedDescription)
    }
    
    func shouldHandleFormPresentation(_ formController: UIViewController!) -> Bool {
        return false
    }
}

