package com.synature.mpos;

import java.util.List;

import com.synature.mpos.datasource.PrintReceiptLogDataSource;
import com.synature.util.Logger;

import android.content.Context;

public class WastePrintReceipt extends PrintReceipt{

	public WastePrintReceipt(Context context, OnPrintReceiptListener listener) {
		super(context, listener);
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		List<PrintReceiptLogDataSource.PrintReceipt> printLogLst = mPrintLog.listPrintReceiptLog();
		for(int i = 0; i < printLogLst.size(); i++){
			PrintReceiptLogDataSource.PrintReceipt printReceipt = printLogLst.get(i);
			try {
				if(Utils.isInternalPrinterSetting(mContext)){
					WintecPrinter wtPrinter = new WintecPrinter(mContext);
					wtPrinter.createTextForPrintWasteReceipt(printReceipt.getTransactionId(), printReceipt.isCopy(), false);
					wtPrinter.print();
				}else{
					EPSONPrinter epPrinter = new EPSONPrinter(mContext);
					epPrinter.createTextForPrintWasteReceipt(printReceipt.getTransactionId(), printReceipt.isCopy(), false);
					epPrinter.print();
				}
				mPrintLog.deletePrintStatus(printReceipt.getPrintId(), printReceipt.getTransactionId());
				
			} catch (Exception e) {
				mPrintLog.updatePrintStatus(printReceipt.getPrintId(), printReceipt.getTransactionId(), PrintReceiptLogDataSource.PRINT_NOT_SUCCESS);
				Logger.appendLog(mContext, 
						MPOSApplication.LOG_PATH, MPOSApplication.LOG_FILE_NAME, 
						" Print receipt fail : " + e.getMessage());
			}
		}
		return null;
	}

}
