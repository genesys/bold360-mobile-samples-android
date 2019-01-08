// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

import UIKit
import Bold360AI

class ViewController: UIViewController {
    
    var chatController: ChatController!
    var chatControllerDelegate: ChatControllerDelegate!
    var chatHandlerProvider: ChatHandlerProvider!
    var chatViewController: UIViewController!

    override func viewDidLoad() {
        super.viewDidLoad()
    }
}

/************************************************************/
// MARK: - Setup Live Chat
/************************************************************/

extension ViewController {
    @IBAction func setupBoldChat(_ sender: Any) {
        // 1. create account & set
        let account = BCAccount(accessKey: "")
        self.chatController = ChatController(account: account)
        // 2.  set controller delegate
        self.chatController.delegate = self
    }
}

/************************************************************/
// MARK: - Setup Bot Chat
/************************************************************/

extension ViewController {
    @IBAction func setupBotChat(_ sender: Any) {
        // 1. create account & set
        let account = self.createAccount()
        self.chatController = ChatController(account: account)
        // 2.  set controller delegate
        self.chatController.delegate = self
    }
    
    func createAccount() -> AccountParams {
        let account = AccountParams()
        account.account = ""
        account.knowledgeBase = ""
        account.apiKey = ""

        return account;
    }
}

/************************************************************/
// MARK: - ChatControllerDelegate
/************************************************************/

extension ViewController: ChatControllerDelegate {
    func didFailLoadChatWithError(_ error: Error!) {
        print(error.localizedDescription)
    }
    
/************************************************************/
// MARK: - View Controller Attachment
/************************************************************/
    
    func shouldPresentChatViewController(_ viewController: UIViewController!) {
        // 4. get chat view controller and show
        self.chatViewController = viewController
        self.navigationController?.pushViewController(viewController, animated: true)
    }
    
/************************************************************/
// MARK: - Preset Custom Form
/************************************************************/
    
    func shouldPresent(_ form: BrandedForm!, handler completionHandler: (((UIViewController & BoldForm)?) -> Void)!) {
        if (completionHandler != nil) {
            if form.form?.type == BCFormTypePostChat {
                let postVC = self.storyboard?.instantiateViewController(withIdentifier: "postChat") as! PostChatViewController
                postVC.form = form
                completionHandler(postVC)
            } else {
                completionHandler(nil)
            }
        }
    }
}
