<form id="uploadForm" action="/datastore-file-access.do" method="POST"
      enctype="multipart/form-data" target="formActionHidden">
    <table class="form" cellpadding="0" cellspacing="9" width="494">
        <tbody>
            <tr>
                <td id="uploadFile">
                    <input type='file' name='myFile' size='45' style="width: 356px;" />
                </td>
                <td>
                    <button type="submit">${submitUploadLabel}</button>
                </td>
            </tr>
        </tbody>
    </table>
</form>
<div id="fileTable"></div>
<div id="filePagination" class="right margin12"></div>
<iframe class="none" id="formActionHidden" name="formActionHidden" src="javascript:''"></iframe>
${plugins}
