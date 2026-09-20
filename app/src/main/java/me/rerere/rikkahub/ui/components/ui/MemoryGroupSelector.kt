package me.rerere.rikkahub.ui.components.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Add01
import me.rerere.hugeicons.stroke.Delete01
import me.rerere.hugeicons.stroke.Edit01
import me.rerere.hugeicons.stroke.MoreVertical
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.data.model.MemoryGroup

@Composable
fun MemoryGroupSelector(
    show: Boolean,
    assistant: Assistant,
    memoryGroups: List<MemoryGroup>,
    onDismiss: () -> Unit,
    onSelectPrivate: () -> Unit,
    onSelectGlobal: () -> Unit,
    onSelectGroup: (MemoryGroup) -> Unit,
    onCreateGroup: (String) -> Unit,
    onRenameGroup: (MemoryGroup, String) -> Unit,
    onDeleteGroup: (MemoryGroup) -> Unit,
) {
    if (!show) return

    var showNameEditor by remember(show) { mutableStateOf(false) }
    var editingGroup by remember(show) { mutableStateOf<MemoryGroup?>(null) }

    if (showNameEditor) {
        MemoryGroupNameDialog(
            group = editingGroup,
            onDismiss = { showNameEditor = false },
            onConfirm = { name ->
                editingGroup?.let { group ->
                    onRenameGroup(group, name)
                } ?: onCreateGroup(name)
                onDismiss()
            },
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.memory_group_select_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                MemoryScopeRow(
                    name = stringResource(R.string.memory_group_private),
                    description = stringResource(R.string.memory_group_private_desc),
                    selected = !assistant.useGlobalMemory && assistant.memoryGroupId == null,
                    onSelect = onSelectPrivate,
                )
                MemoryScopeRow(
                    name = stringResource(R.string.memory_group_global),
                    description = stringResource(R.string.memory_group_global_desc),
                    selected = assistant.useGlobalMemory,
                    label = stringResource(R.string.memory_group_builtin),
                    onSelect = onSelectGlobal,
                )
                memoryGroups.forEach { group ->
                    MemoryScopeRow(
                        name = group.name,
                        selected = !assistant.useGlobalMemory && assistant.memoryGroupId == group.id,
                        onSelect = { onSelectGroup(group) },
                        actions = {
                            MemoryGroupActions(
                                onRename = {
                                    editingGroup = group
                                    showNameEditor = true
                                },
                                onDelete = { onDeleteGroup(group) },
                            )
                        },
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                TextButton(
                    onClick = {
                        editingGroup = null
                        showNameEditor = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(HugeIcons.Add01, contentDescription = null)
                    Text(
                        text = stringResource(R.string.memory_group_create),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
private fun MemoryScopeRow(
    name: String,
    selected: Boolean,
    onSelect: () -> Unit,
    description: String? = null,
    label: String? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
        ) {
            Text(
                text = name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp),
            )
        }
        actions?.invoke()
    }
}

@Composable
private fun MemoryGroupActions(
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = HugeIcons.MoreVertical,
                contentDescription = stringResource(R.string.memory_group_more_actions),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.memory_group_rename)) },
                leadingIcon = { Icon(HugeIcons.Edit01, contentDescription = null) },
                onClick = {
                    expanded = false
                    onRename()
                },
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.memory_group_delete),
                        color = MaterialTheme.colorScheme.error,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = HugeIcons.Delete01,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                },
                onClick = {
                    expanded = false
                    onDelete()
                },
            )
        }
    }
}

@Composable
private fun MemoryGroupNameDialog(
    group: MemoryGroup?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember(group?.id) { mutableStateOf(group?.name.orEmpty()) }
    val normalizedName = name.trim()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (group == null) R.string.memory_group_create else R.string.memory_group_rename
                )
            )
        },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.memory_group_name_hint)) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                enabled = normalizedName.isNotEmpty(),
                onClick = { onConfirm(normalizedName) },
            ) {
                Text(stringResource(R.string.assistant_page_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
