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
// MARK: - Chat Life Cycle Sample
/************************************************************/
    
    func didUpdate(_ state: ChatState, with agentType: AgentType) {
        if (agentType == AgentType.Live) {
            if (ChatState.started == state) {
                self.chatViewController.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "End Chat", style: .plain, target: self, action:#selector(buttonAction))
            } else if (ChatState.ended == state) {
                self.chatViewController.navigationItem.rightBarButtonItem = nil
            }
        }
    }
    
    @objc func buttonAction(sender: UIButton!) {
        self.chatController.endChat()
    }
}
