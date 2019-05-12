// ===================================================================================================
// Copyright © 2019 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

import UIKit
import Bold360AI

class AgentViewController: BotDemoViewController {

    
    override func createAccount() -> Account {
        let liveAccount = LiveAccount()
        liveAccount.apiKey = "2300000001700000000:2279145895771367548:MGfXyj9naYgPjOZBruFSykZjIRPzT1jl"
        return liveAccount
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
    }
    
    @objc func endChat(_ sender: UIBarButtonItem) {
        self.chatController.endChat()
    }
    

    override func shouldPresentChatViewController(_ viewController: UINavigationController!) {
        super.shouldPresentChatViewController(viewController)
        viewController.viewControllers.first?.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "End Chat", style: .plain, target: self, action: #selector(AgentViewController.endChat(_:)))
    }
}
