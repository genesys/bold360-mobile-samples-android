// ===================================================================================================
// Copyright © 2018 nanorep.
// NanorepUI SDK.
// All rights reserved.
// ===================================================================================================

import UIKit
import Bold360AI

class OptionsCell: UITableViewCell {
    
    @IBOutlet weak var optionsBtn: UIButton!
    
    var actionSheetController: UIAlertController!
    var showOptionsCallback: ((_ vc: UIViewController)->())!
    
    @IBAction func showOptions(_ sender: Any) {
        self.showOptionsCallback(actionSheetController)
    }
    
    func fillOptions(field:BCFormField, completion: @escaping (_ vc: UIViewController)->()) {
        self.showOptionsCallback = completion
        self.actionSheetController = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        
        for option: BCFormFieldOption in field.options as! Array<BCFormFieldOption> {
            let action: UIAlertAction = UIAlertAction(title: option.name, style: .default) { action -> Void in
                field.value = option.value
                self.optionsBtn.titleLabel?.text = option.name
            }
            
            actionSheetController.addAction(action)
        }
        
        let cancelAction: UIAlertAction = UIAlertAction(title: "Cancel", style: .cancel) { action -> Void in }
        actionSheetController.addAction(cancelAction)
    }
    
    
    
}
