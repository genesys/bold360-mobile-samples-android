// ===================================================================================================
// Copyright © 2019 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

import Foundation
import CoreData
import Bold360AI


public class ChatConfiguration: NSManagedObject {
    var configuration: ChatElementConfiguration! {
        set {
            guard newValue != nil else {
                return
            }
            self.position = Int16(newValue.avatarPosition.rawValue)
            self.textColorHex = newValue.textColor.toHex
            if let img = newValue.avatar, let iconData = img.pngData() as NSData? {
                self.avatarData = iconData
            }
        }
        get {
            let config = ChatElementConfiguration()
            if let imgData = self.avatarData as Data? {
                config.avatar = UIImage(data: imgData)
            }
            config.avatarPosition = AvatarPosition(rawValue: Int(self.position)) ?? AvatarPosition.bottomLeft
            config.textColor = UIColor(hex: self.textColorHex ?? "#000000")
            return config
        }
    }
}

extension UIColor {
    
    convenience init?(hex: String) {
        var hexNormalized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        hexNormalized = hexNormalized.replacingOccurrences(of: "#", with: "")
        
        // Helpers
        var rgb: UInt32 = 0
        var r: CGFloat = 0.0
        var g: CGFloat = 0.0
        var b: CGFloat = 0.0
        var a: CGFloat = 1.0
        let length = hexNormalized.count
        
        // Create Scanner
        Scanner(string: hexNormalized).scanHexInt32(&rgb)
        
        if length == 6 {
            r = CGFloat((rgb & 0xFF0000) >> 16) / 255.0
            g = CGFloat((rgb & 0x00FF00) >> 8) / 255.0
            b = CGFloat(rgb & 0x0000FF) / 255.0
            
        } else if length == 8 {
            r = CGFloat((rgb & 0xFF000000) >> 24) / 255.0
            g = CGFloat((rgb & 0x00FF0000) >> 16) / 255.0
            b = CGFloat((rgb & 0x0000FF00) >> 8) / 255.0
            a = CGFloat(rgb & 0x000000FF) / 255.0
            
        } else {
            return nil
        }
        
        self.init(red: r, green: g, blue: b, alpha: a)
    }
    
    var toHex: String? {
        guard let components = cgColor.components, components.count >= 3 else {
            return nil
        }
        
        // Helpers
        let r = Float(components[0])
        let g = Float(components[1])
        let b = Float(components[2])
        var a = Float(1.0)
        
        if components.count >= 4 {
            a = Float(components[3])
        }
        
        // Create Hex String
        let hex = String(format: "%02lX%02lX%02lX%02lX", lroundf(r * 255), lroundf(g * 255), lroundf(b * 255), lroundf(a * 255))
        
        return hex
    }
}

