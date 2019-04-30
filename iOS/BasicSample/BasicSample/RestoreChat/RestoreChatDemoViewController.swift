//
//  RestoreChatDemoViewController.swift
//  BasicSample
//
//  Created by Nissim Pardo on 29/04/2019.
//  Copyright © 2019 bold360ai. All rights reserved.
//

import UIKit
import Bold360AI

class RestoreChatDemoViewController: AgentViewController {

    var restoreChat: RestoreChat?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        if let appDelegate = UIApplication.shared.delegate as? AppDelegate {
            self.restoreChat = RestoreChat(appDelegate.persistentContainer.viewContext)
            self.restoreChat?.groupId = "RestoreChatDemoViewController"
            self.restoreChat?.delegate = self
            self.chatController.historyProvider = self.restoreChat
        }
    }
    
    override func shouldPresentChatViewController(_ viewController: UINavigationController!) {
        super.shouldPresentChatViewController(viewController)
        self.restoreChat?.state = .prsented
    }
    
    override func dismissChat(_ sender: UIBarButtonItem?) {
        self.navigationController?.presentedViewController?.dismiss(animated: false, completion: nil)
        self.restoreChat?.state = .dismissed
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}

extension RestoreChatDemoViewController: RestoreChatDelegate {
    func didReceiveBackgroundMessage(_ message: StorableChatElement) {
        let alert = UIAlertController(title: "Agent Message", message: message.text, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Load Chat", style: .default, handler: { _ in
            self.chatController.restoreChatViewController()
        }))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        self.navigationController?.present(alert, animated: true, completion: nil)
    }
}
