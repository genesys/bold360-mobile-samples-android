// ===================================================================================================
// Copyright © 2019 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

import UIKit

class DemoTableViewCell: UITableViewCell {
    @IBOutlet weak var iconImageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    
    var model: [String: String]! {
        didSet {
            if let icon = self.model["icon"] {
                self.iconImageView.image = UIImage(named: icon)
            }
            self.titleLabel.text = self.model["title"]
        }
    }

    

}
