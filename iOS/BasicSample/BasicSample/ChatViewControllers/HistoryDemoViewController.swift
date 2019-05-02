// ===================================================================================================
// Copyright © 2019 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

import UIKit
import CoreData

class HistoryDemoViewController: BotDemoViewController {

    var restoreChat: RestoreChat?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        if let appDelegate = UIApplication.shared.delegate as? AppDelegate {
            self.restoreChat = RestoreChat(appDelegate.persistentContainer.viewContext)
            self.restoreChat?.groupId = "HistoryDemoViewController"
            self.chatController.historyProvider = self.restoreChat
        }
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
