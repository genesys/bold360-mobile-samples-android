// ===================================================================================================
// Copyright © 2018 nanorep.
// NanorepUI SDK.
// All rights reserved.
// ===================================================================================================

import UIKit
import Bold360AI

class PostChatViewController: UITableViewController {
    @IBOutlet var formTitle: UILabel!
    @IBOutlet weak var startBtn: UIButton!
    
    @IBAction func startTapped(_ sender: Any) {
        self.delegate.submitForm(formInfo)
    }
    
    override func viewDidLoad() {
        self.formTitle.text = self.formInfo.globalBranding["api#postchat#intro"] as? String
        self.startBtn.setTitle(self.formInfo.globalBranding["api#postchat#done"] as? String, for: .normal)
        self.startBtn.setTitle(self.formInfo.globalBranding["api#postchat#done"] as? String, for: .focused)
    }
    
    var formInfo: BrandedForm!
    var formDelegate: BoldFormDelegate!
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return formInfo.form.formFields.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let field = formInfo.form?.formFields[indexPath.row] as? BCFormField {
            if field.type == BCFormFieldTypeEmail {
                let cell: TextCell = tableView.dequeueReusableCell(withIdentifier: "TextCell", for: indexPath) as! TextCell
                cell.txtField.placeholder = field.label
                return cell
            } else if field.type == BCFormFieldTypeRadio {
                let cell: OptionsCell = tableView.dequeueReusableCell(withIdentifier: "OptionsCell", for: indexPath) as! OptionsCell
                cell.optionsBtn.setTitle(field.label, for: .normal)
                cell.optionsBtn.setTitle(field.label, for: .focused)
                cell.fillOptions(field: field) { (vc) in
                    self.present(vc, animated: true, completion: nil)
                }
                return cell
            } else {
                let cell: RatingCell = tableView.dequeueReusableCell(withIdentifier: "RatingCell", for: indexPath) as! RatingCell
                cell.ratingTitle.text = field.label
                return cell
            }
        }
        
        return UITableViewCell()
    }
}

extension PostChatViewController: BoldForm {
    var form: BrandedForm! {
        get {
            return formInfo
        }
        set(form) {
            formInfo = form
        }
    }
    
    var delegate: BoldFormDelegate! {
        get {
            return formDelegate
        }
        set(delegate) {
            formDelegate = delegate
        }
    }
}
